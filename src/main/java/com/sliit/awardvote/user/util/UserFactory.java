package com.sliit.awardvote.user.util;

import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import org.springframework.stereotype.Component;

/**
 * UserFactory - Factory Method design pattern.
 *
 * Creates User objects in one place so the account rules are not repeated
 * across the code: self-registered users are always PUBLIC_USER and start
 * inactive (until OTP verification), while system-created users start active.
 */
@Component
public class UserFactory {

    /** Self-registration: role is fixed to PUBLIC_USER and the account stays inactive until verified. */
    public User createPublicUser(String fullName, String email, String username, String rawPassword) {
        User user = new User(fullName, email, username, rawPassword, UserRole.PUBLIC_USER);
        user.setActive(false);
        return user;
    }

    /** System / admin-created account with the given role, active immediately. */
    public User createUser(UserRole role, String fullName, String email, String username, String rawPassword) {
        User user = new User(fullName, email, username, rawPassword, role);
        user.setActive(true);
        return user;
    }
}
