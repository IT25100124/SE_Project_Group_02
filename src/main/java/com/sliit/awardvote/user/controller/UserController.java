package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.service.RoleService;
import com.sliit.awardvote.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    
    // Constructor to inject UserService and RoleService
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Check whether the logged-in user has permission to manage users

    private boolean canManageUsers(HttpSession session) {
        User u = SessionUtil.currentUser(session);

        // Return true if user exists and has MANAGE_USERS permission
        return u != null && u.hasPermission(Permission.MANAGE_USERS);
    }

      // Find and display all users
    @GetMapping
    public String list(Model model, HttpSession session) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("customRoles", roleService.findAll());
        return "users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        model.addAttribute("user", userService.findById(id).orElseThrow());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("customRoles", roleService.findAll());
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user,
                        @RequestParam(required = false) Long customRoleId,
                        HttpSession session) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        user.setCustomRole(customRoleId != null ? roleService.findById(customRoleId).orElse(null) : null);
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, HttpSession session) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        userService.toggleActive(id);
        return "redirect:/users";
    }

    /** Permanent delete by an admin / user manager. POST-only so a link or prefetch can never trigger it. */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        User actor = SessionUtil.currentUser(session);

        if (actor.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error",
                    "To delete your own account, use My Profile > Delete My Account (it asks for your password).");
            return "redirect:/users";
        }

        User target = userService.findById(id).orElse(null);
        if (target == null) {
            redirectAttributes.addFlashAttribute("error", "That account no longer exists.");
            return "redirect:/users";
        }

        Optional<String> refusal = userService.validateDeletion(actor, target);
        if (refusal.isPresent()) {
            redirectAttributes.addFlashAttribute("error", refusal.get());
            return "redirect:/users";
        }

        userService.deletePermanently(id);
        redirectAttributes.addFlashAttribute("success",
                "Account \"" + target.getUsername() + "\" was permanently deleted.");
        return "redirect:/users";
    }
}
