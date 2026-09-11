package com.sliit.awardvote.user;

import com.sliit.awardvote.common.AbstractCrudService;
import com.sliit.awardvote.common.PasswordUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractCrudService<User, Long> {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    /** Hook override: always hash a raw password before it is persisted. */
    @Override
    protected void beforeSave(User user) {
        if (user.getPassword() != null && user.getPassword().length() != 64) {
            // 64 hex chars == already a SHA-256 hash, otherwise treat as raw input
            user.setPassword(PasswordUtil.hash(user.getPassword()));
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** Used by the forgot-password flow, where the person may enter either their username or email. */
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier));
    }

    public boolean usernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(User::isActive)
                .filter(u -> PasswordUtil.matches(rawPassword, u.getPassword()));
    }

    public void toggleActive(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setActive(!u.isActive());
            userRepository.save(u);
        });
    }

    /**
     * Guarantees the account ends up inactive, regardless of its current state -
     * unlike toggleActive(), which flips whatever it currently is. Used as the
     * fallback when a self-service or admin delete is blocked by related records.
     */
    public void deactivate(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setActive(false);
            userRepository.save(u);
        });
    }
}
