package com.sliit.awardvote.sponsor.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.dao.SponsorDao;

import org.springframework.stereotype.Service;

@Service
public class SponsorService extends AbstractCrudService<Sponsor, Long> {

    private final SponsorDao sponsorDao;

    public SponsorService(SponsorDao sponsorDao) {
        this.sponsorDao = sponsorDao;
    }

    @Override
    protected GenericDao<Sponsor, Long> getDao() {
        return sponsorDao;
    }
}
