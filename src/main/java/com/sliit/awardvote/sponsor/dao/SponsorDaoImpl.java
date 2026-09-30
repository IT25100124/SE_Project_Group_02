package com.sliit.awardvote.sponsor.dao;

import com.sliit.awardvote.common.dao.AbstractJpaDao;
import com.sliit.awardvote.sponsor.model.Sponsor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class SponsorDaoImpl extends AbstractJpaDao<Sponsor> implements SponsorDao {

    public SponsorDaoImpl() {
        super(Sponsor.class);
    }
}
