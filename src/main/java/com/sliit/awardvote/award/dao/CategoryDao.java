package com.sliit.awardvote.award.dao;

//The Category model/entity is imported to be handled by this DAO.
import com.sliit.awardvote.award.model.Category;
//Inherit the GenericDao interface
import com.sliit.awardvote.common.dao.GenericDao;

import java.util.List;

/** Data access contract for {@link Category}. */
//An interface that defines the methods required to access category data from the database.
public interface CategoryDao extends GenericDao<Category, Long> {
    List<Category> findByAwardProgrammeId(Long programmeId);
}
