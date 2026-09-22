package com.sliit.awardvote.user.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.user.model.Role;
import com.sliit.awardvote.user.dao.RoleDao;

import org.springframework.stereotype.Service;

@Service
public class RoleService extends AbstractCrudService<Role, Long> {

    private final RoleDao roleDao;

    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    protected GenericDao<Role, Long> getDao() {
        return roleDao;
    }

    public boolean nameTaken(String name) {
        return roleDao.existsByName(name);
    }
}
