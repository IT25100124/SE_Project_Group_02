package com.sliit.awardvote.award.dao;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.common.dao.GenericDao;

/** Data access contract for {@link AwardProgramme}. */
public interface AwardDao extends GenericDao<AwardProgramme, Long> {
}
// GenericDao -- Inherit code
//AwardDao--save() / findbyID() / delete()