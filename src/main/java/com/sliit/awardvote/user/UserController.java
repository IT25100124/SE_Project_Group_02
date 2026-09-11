package com.sliit.awardvote.user;

import com.sliit.awardvote.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    private boolean canManageUsers(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_USERS);
    }

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

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!canManageUsers(session)) return "redirect:/dashboard";
        try {
            userService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // This account has related records (nominations submitted, votes cast, notifications
            // received, feedback, etc.) - the database rightly refuses to delete it out from under
            // that history. Deactivating (Toggle) is the correct move for an account like this.
            redirectAttributes.addFlashAttribute("error",
                    "Can't delete this user - they have related records (nominations, votes, notifications, "
                            + "or similar). Use \"Toggle\" to deactivate the account instead, which keeps their history intact.");
        }
        return "redirect:/users";
    }
}
