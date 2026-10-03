package com.sliit.awardvote.common.dao;

import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * AbstractJpaDao - reusable JPA implementation of {@link GenericDao}.
 *
 * OOP concept: ABSTRACTION + INHERITANCE
 * The basic CRUD plumbing is written once here against the JPA
 * {@link EntityManager}; each concrete DAO (UserDaoImpl, VoteDaoImpl ...)
 * inherits it and only adds its own JPQL queries.
 */
public abstract class AbstractJpaDao<T extends BaseEntity> implements GenericDao<T, Long> {

    @PersistenceContext
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public T save(T entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        }
        return em.merge(entity);
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("select e from " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public long count() {
        return em.createQuery("select count(e) from " + entityClass.getSimpleName() + " e", Long.class)
                .getSingleResult();
    }
}
