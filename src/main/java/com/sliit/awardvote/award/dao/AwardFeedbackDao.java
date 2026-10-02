package com.sliit.awardvote.award.dao;

import com.sliit.awardvote.award.model.AwardFeedback;
import com.sliit.awardvote.common.dao.GenericDao;

import java.util.List;

/** Data access contract for {@link AwardFeedback}. */
public interface AwardFeedbackDao extends GenericDao<AwardFeedback, Long> {
    /** All feedback left on one award programme, newest first. */
    List<AwardFeedback> findByAwardProgrammeId(Long programmeId);
}
