package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserDaoImpl extends AbstractJpaDao<User> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return em.createQuery("select e from User e where e.username = :username", User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return em.createQuery("select e from User e where e.email = :email", User.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsByUsername(String username) {
        return em.createQuery("select count(e) from User e where e.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return em.createQuery("select count(e) from User e where e.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult() > 0;
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return em.createQuery("select e from User e where e.role = :role", User.class)
                .setParameter("role", role)
                .getResultList();
    }

    @Override
    public List<User> findByCustomRoleId(Long roleId) {
        return em.createQuery("select e from User e where e.customRole.id = :roleId", User.class)
                .setParameter("roleId", roleId)
                .getResultList();
    }
}
