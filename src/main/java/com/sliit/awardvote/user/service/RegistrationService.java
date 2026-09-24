package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private final UserService userService;
    private final OtpService otpService;

    public RegistrationService(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    
    // Creates the account (inactive, PUBLIC_USER) and sends the first
    
    public User register(User newUser, NotificationType channel) {
        newUser.setRole(UserRole.PUBLIC_USER);
        newUser.setActive(false); // stays disabled until the OTP is verified
        User saved = userService.save(newUser);
        sendVerificationCode(saved, channel);
        return saved;
    }
// verification code. Caller is responsible for checking username/email
    public void resendCode(String identifier, NotificationType channel) {
        userService.findByUsernameOrEmail(identifier)
                .filter(u -> !u.isActive())
                .ifPresent(u -> sendVerificationCode(u, channel));
    }

    private void sendVerificationCode(User user, NotificationType channel) {
        String message = "Welcome to Award Vote Lanka! Your account verification code is {code}. "
                + "It expires in {minutes} minutes.";
        otpService.issueAndSend(user, OtpPurpose.ACCOUNT_VERIFICATION, channel, message);
    }

    public Optional<User> verifyAndActivate(String identifier, String code) {
        Optional<User> userOpt = userService.findByUsernameOrEmail(identifier);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (user.isActive()) {
            return Optional.of(user);
        }
        if (!otpService.verify(user, code, OtpPurpose.ACCOUNT_VERIFICATION)) {
            return Optional.empty();
        }
        user.setActive(true);
        userService.save(user);
        return Optional.of(user);
    }
}
