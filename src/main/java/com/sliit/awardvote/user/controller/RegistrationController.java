package com.sliit.awardvote.user.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.service.RegistrationService;
import com.sliit.awardvote.user.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/** Registration (OTP-verified via Email or SMS). */
@Controller
public class RegistrationController {

    /** Shortest password accepted when creating an account. */
    private static final int MIN_PASSWORD_LENGTH = 5;

    private final UserService userService;
    private final RegistrationService registrationService;

    public RegistrationController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                            @RequestParam NotificationType channel,
                            Model model) {
        if (user.getPassword() == null || user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            model.addAttribute("error", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            return "register";
        }
        if (userService.usernameTaken(user.getUsername())) {
            model.addAttribute("error", "That username is already taken.");
            return "register";
        }
        if (userService.emailTaken(user.getEmail())) {
            model.addAttribute("error", "That email is already registered.");
            return "register";
        }
        try {
            registrationService.register(user, channel);
        } catch (DataIntegrityViolationException ex) {
            // Race condition: another request took this username/email between our
            // check above and the insert committing. The DB's unique constraint is
            // what actually caught it - this just turns that into a normal message
            // instead of letting the exception bubble up to an error page.
            model.addAttribute("error", "That username or email was just taken by someone else. Please try a different one.");
            return "register";
        }
        model.addAttribute("identifier", user.getUsername());
        model.addAttribute("channel", channel);
        return "verify-account";
    }

    @PostMapping("/verify-account")
    public String verifyAccount(@RequestParam String identifier,
                                 @RequestParam String code,
                                 HttpSession session,
                                 Model model) {
        Optional<User> verified = registrationService.verifyAndActivate(identifier, code);
        if (verified.isEmpty()) {
            model.addAttribute("identifier", identifier);
            model.addAttribute("error", "That code is invalid or has expired. Please request a new one.");
            return "verify-account";
        }
        // Verified accounts are logged straight in - no need to make them log in a second time.
        SessionUtil.login(session, verified.get());
        return "redirect:/dashboard?verified";
    }

    @PostMapping("/verify-account/resend")
    public String resendVerificationCode(@RequestParam String identifier,
                                          @RequestParam NotificationType channel,
                                          Model model) {
        registrationService.resendCode(identifier, channel);
        model.addAttribute("identifier", identifier);
        model.addAttribute("channel", channel);
        model.addAttribute("resent", true);
        return "verify-account";
    }
}
