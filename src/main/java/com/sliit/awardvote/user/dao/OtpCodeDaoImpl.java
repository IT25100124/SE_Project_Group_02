package com.sliit.awardvote.user.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class OtpCodeDaoImpl extends AbstractJpaDao<OtpCode> implements OtpCodeDao {

    public OtpCodeDaoImpl() {
        super(OtpCode.class);
    }

    @Override
    public List<OtpCode> findByUserIdAndPurposeAndUsedFalse(Long userId, OtpPurpose purpose) {
        return em.createQuery("select e from OtpCode e where e.user.id = :userId and e.purpose = :purpose and e.used = false", OtpCode.class)
                .setParameter("userId", userId)
                .setParameter("purpose", purpose)
                .getResultList();
    }
}
