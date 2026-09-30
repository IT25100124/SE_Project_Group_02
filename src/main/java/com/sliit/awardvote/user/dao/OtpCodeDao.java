package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;

import java.util.List;

/** Data access contract for {@link OtpCode}. */
public interface OtpCodeDao extends GenericDao<OtpCode, Long> {
    List<OtpCode> findByUserIdAndPurposeAndUsedFalse(Long userId, OtpPurpose purpose);
}
