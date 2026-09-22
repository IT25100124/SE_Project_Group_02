package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.ProfileService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
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
        User user = profileService.getUser(current.getId());

        Optional<String> error = profileService.validateUpdate(user, username, email);
        if (error.isPresent()) {
            model.addAttribute("user", user);
            model.addAttribute("error", error.get());
            return "users/profile";
        }

        User saved = profileService.updateProfile(user, fullName, email, phone, username, newPassword);
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
        User user = profileService.getUser(current.getId());

        if (!profileService.deleteAccount(user, currentPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("error", "That password is incorrect - your account was not deleted.");
            return "users/profile";
        }

        SessionUtil.logout(session);
        redirectAttributes.addFlashAttribute("accountDeleted", true);
        return "redirect:/login";
    }
}
