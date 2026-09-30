package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import java.util.List;
import java.util.Optional;

/** Data access contract for {@link User}. */
public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByCustomRoleId(Long roleId);

    long countByRole(UserRole role);

    /**
     * Permanently removes the user row from the database, in one transaction.
     * Rows that cannot exist without the user (votes, judge scores, notifications,
     * OTP codes, award comments) are deleted with it; records that belong to the
     * organisation (nominations, feedback, announcements) are kept but detached
     * from the deleted user.
     */
    void deletePermanently(Long userId);
}
