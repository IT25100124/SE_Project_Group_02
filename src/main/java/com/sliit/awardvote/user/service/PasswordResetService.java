package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * "Forgot password" flow. Code generation, dispatch and verification are
 * delegated to {@link OtpService} (purpose = PASSWORD_RESET); this class
 * only knows the password-reset-specific parts: looking the user up by
 * username/email, and actually changing the password once a code checks out.
 *
 * Deliberately never reveals whether a given username/email is registered:
 * {@link #requestReset} returns normally either way, so the controller can
 * show one uniform message and avoid leaking account existence to an
 * attacker probing the form.
 */
@Service
public class PasswordResetService {

    private final UserService userService;
    private final OtpService otpService;

    public PasswordResetService(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    public void requestReset(String identifier, NotificationType channel) {
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);
        if (userOpt.isEmpty()) {
            return; // silently no-op - see class javadoc
        }
        String message = "Your Award Vote Lanka password reset code is {code}. It expires in {minutes} minutes. "
                + "If you didn't request this, you can safely ignore it.";
        otpService.issueAndSend(userOpt.get(), OtpPurpose.PASSWORD_RESET, channel, message);
    }

    /** Verifies the code and, if valid, replaces the user's password. Returns false on any mismatch, expiry, or unknown identifier. */
    public boolean verifyAndReset(String identifier, String code, String newPassword) {
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!otpService.verify(user, code, OtpPurpose.PASSWORD_RESET)) {
            return false;
        }
        user.setPassword(newPassword); // raw value - UserService#beforeSave hashes it on save
        userService.save(user);
        return true;
    }
}
