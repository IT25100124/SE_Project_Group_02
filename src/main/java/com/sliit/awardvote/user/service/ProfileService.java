package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.util.PasswordUtil;
import com.sliit.awardvote.user.model.User;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Self-service account changes: edit own profile, delete own account. */
@Service
public class ProfileService {

    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }

    public User getUser(Long userId) {
        return userService.findById(userId).orElseThrow();
    }

    /** Returns an error message if the new username/email clashes with another account, otherwise empty. */
    public Optional<String> validateUpdate(User user, String username, String email) {
        if (!username.equals(user.getUsername()) && userService.usernameTaken(username)) {
            return Optional.of("That username is already taken.");
        }
        if (!email.equals(user.getEmail()) && userService.emailTaken(email)) {
            return Optional.of("That email is already registered.");
        }
        return Optional.empty();
    }

    public User updateProfile(User user, String fullName, String email, String phone,
                              String username, String newPassword) {
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUsername(username);
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword);
        }
        return userService.save(user);
    }

    /**
     * Deletes the account after re-checking the password. If other records still
     * reference the user, the account is deactivated instead. Returns false when
     * the password is wrong (nothing is changed).
     */
    public boolean deleteAccount(User user, String currentPassword) {
        if (!PasswordUtil.matches(currentPassword, user.getPassword())) {
            return false;
        }
        try {
            userService.deleteById(user.getId());
        } catch (DataIntegrityViolationException e) {
            userService.deactivate(user.getId());
        }
        return true;
    }
}
