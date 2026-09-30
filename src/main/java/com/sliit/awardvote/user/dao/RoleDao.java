package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.Role;

import java.util.Optional;

/** Data access contract for {@link Role}. */
public interface RoleDao extends GenericDao<Role, Long> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
