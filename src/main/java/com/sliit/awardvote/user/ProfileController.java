package com.sliit.awardvote.user;

import com.sliit.awardvote.common.PasswordUtil;
import com.sliit.awardvote.common.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String view(Model model, HttpSession session) {
        model.addAttribute("user", SessionUtil.currentUser(session));
        return "users/profile";
    }

    @PostMapping("/update")
    public String update(@RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam(required = false) String phone,
                          @RequestParam String username,
                          @RequestParam(required = false) String newPassword,
                          HttpSession session,
                          Model model) {
        User current = SessionUtil.currentUser(session);
        User user = userService.findById(current.getId()).orElseThrow();

        // Only flag as "taken" if the value actually changed - otherwise checking
        // a user's own unchanged username/email against itself always looks taken.
        if (!username.equals(user.getUsername()) && userService.usernameTaken(username)) {
            model.addAttribute("user", user);
            model.addAttribute("error", "That username is already taken.");
            return "users/profile";
        }
        if (!email.equals(user.getEmail()) && userService.emailTaken(email)) {
            model.addAttribute("user", user);
            model.addAttribute("error", "That email is already registered.");
            return "users/profile";
        }

        // Fetch-then-patch only the editable fields - role, customRole and active
        // are never read from this form and are left exactly as they were.
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUsername(username);
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword); // raw value - UserService#beforeSave hashes it on save
        }
        User saved = userService.save(user);

        // The session holds its own cached copy of the user - refresh it so the
        // navbar/dashboard reflect the change immediately without a re-login.
        SessionUtil.login(session, saved);

        model.addAttribute("user", saved);
        model.addAttribute("success", "Your profile has been updated.");
        return "users/profile";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String currentPassword,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        User current = SessionUtil.currentUser(session);
        User user = userService.findById(current.getId()).orElseThrow();

        if (!PasswordUtil.matches(currentPassword, user.getPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("error", "That password is incorrect - your account was not deleted.");
            return "users/profile";
        }

        try {
            userService.deleteById(user.getId());
        } catch (DataIntegrityViolationException e) {
       
            userService.deactivate(user.getId());
        }

        SessionUtil.logout(session);
        redirectAttributes.addFlashAttribute("accountDeleted", true);
        return "redirect:/login";
    }
}
