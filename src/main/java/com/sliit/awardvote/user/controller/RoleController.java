package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.Role;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.dao.UserDao;
import com.sliit.awardvote.user.service.RoleService;
import com.sliit.awardvote.user.util.DefaultRolePermissions;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MODULE 1: USER MANAGEMENT — Roles & Permissions
 *
 * Lets an administrator create named roles, choose exactly which
 * {@link Permission}s each one grants, and assign roles to user accounts
 * (see {@link UserController#save}). Gated behind {@code MANAGE_ROLES},
 * which only SYSTEM_ADMIN carries by default.
 */
@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    private final UserDao userDao;

    public RoleController(RoleService roleService, UserDao userDao) {
        this.roleService = roleService;
        this.userDao = userDao;
    }

    private boolean canManageRoles(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_ROLES);
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        if (!canManageRoles(session)) return "redirect:/dashboard";
        model.addAttribute("roles", roleService.findAll());

        // Built-in role defaults, precomputed here so the view never needs static-method lookups.
        Map<UserRole, Set<Permission>> builtInDefaults = new LinkedHashMap<>();
        for (UserRole ur : UserRole.values()) {
            builtInDefaults.put(ur, DefaultRolePermissions.forRole(ur));
        }
        model.addAttribute("builtInDefaults", builtInDefaults);
        return "roles/list";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        if (!canManageRoles(session)) return "redirect:/dashboard";
        model.addAttribute("role", new Role());
        model.addAttribute("allPermissions", Permission.values());
        return "roles/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManageRoles(session)) return "redirect:/dashboard";
        model.addAttribute("role", roleService.findById(id).orElseThrow());
        model.addAttribute("allPermissions", Permission.values());
        return "roles/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Role role, HttpSession session) {
        if (!canManageRoles(session)) return "redirect:/dashboard";
        roleService.save(role);
        return "redirect:/roles";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!canManageRoles(session)) return "redirect:/dashboard";
        // Unassign this role from any user holding it before deleting, so the
        // foreign key never dangles and no account is silently broken.
        List<User> holders = userDao.findByCustomRoleId(id);
        for (User u : holders) {
            u.setCustomRole(null);
            userDao.save(u);
        }
        roleService.deleteById(id);
        return "redirect:/roles";
    }
}
