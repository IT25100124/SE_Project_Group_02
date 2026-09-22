package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.common.util.PasswordUtil;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.dao.UserDao;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends AbstractCrudService<User, Long> {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    protected GenericDao<User, Long> getDao() {
        return userDao;
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
        return userDao.findByUsername(username);
    }

    /** Used by the forgot-password flow, where the person may enter either their username or email. */
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userDao.findByUsername(identifier)
                .or(() -> userDao.findByEmail(identifier));
    }

    public boolean usernameTaken(String username) {
        return userDao.existsByUsername(username);
    }

    public boolean emailTaken(String email) {
        return userDao.existsByEmail(email);
    }

    public List<User> findByRole(UserRole role) {
        return userDao.findByRole(role);
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userDao.findByUsername(username)
                .filter(User::isActive)
                .filter(u -> PasswordUtil.matches(rawPassword, u.getPassword()));
    }

    public void toggleActive(Long userId) {
        userDao.findById(userId).ifPresent(u -> {
            u.setActive(!u.isActive());
            userDao.save(u);
        });
    }

    public void deactivate(Long userId) {
        userDao.findById(userId).ifPresent(u -> {
            u.setActive(false);
            userDao.save(u);
        });
    }
}
