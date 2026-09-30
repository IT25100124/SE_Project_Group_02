package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.util.PasswordUtil;
import com.sliit.awardvote.user.model.User;

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
     * Permanently deletes the person's own account after re-checking their
     * password. Returns an error message if the deletion was refused (wrong
     * password, or they are the last system administrator), or empty on success.
     */
    public Optional<String> deleteAccount(User user, String currentPassword) {
        if (!PasswordUtil.matches(currentPassword, user.getPassword())) {
            return Optional.of("That password is incorrect - your account was not deleted.");
        }
        Optional<String> refusal = userService.validateDeletion(user, user);
        if (refusal.isPresent()) {
            return refusal;
        }
        userService.deletePermanently(user.getId());
        return Optional.empty();
    }
}
