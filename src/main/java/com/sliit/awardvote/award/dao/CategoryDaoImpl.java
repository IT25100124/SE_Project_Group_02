package com.sliit.awardvote.award.dao;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.common.dao.AbstractJpaDao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//making CategoryDaoImpl a Spring Repository bean.
@Repository
//This indicates that the operations of this DAO are executed within a read-only transaction.
@Transactional(readOnly = true)
public class CategoryDaoImpl extends AbstractJpaDao<Category> implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);//Parent class constructor
    }

    @Override
    public List<Category> findByAwardProgrammeId(Long programmeId) {
        return em.createQuery("select e from Category e where e.awardProgramme.id = :programmeId", Category.class)
                .setParameter("programmeId", programmeId)
                .getResultList();
    }
}
