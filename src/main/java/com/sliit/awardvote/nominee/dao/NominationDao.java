package com.sliit.awardvote.nominee.dao;

import com.sliit.awardvote.common.dao.GenericDao; //contains operations
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.model.NominationStatus;

import java.util.List;  //some methods return multiple nominations

//defines the database-access operations for Nomination
public interface NominationDao extends GenericDao<Nomination, Long> {
    List<Nomination> findByCategoryId(Long categoryId);

    List<Nomination> findByStatus(NominationStatus status);

    List<Nomination> findBySubmittedById(Long userId);

    //how many nominations have that status.
    long countByStatus(NominationStatus status);
}
