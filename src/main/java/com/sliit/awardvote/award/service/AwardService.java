package com.sliit.awardvote.award.service;

import com.sliit.awardvote.award.event.AwardStatusChangedEvent;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.dao.AwardDao;
import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AwardService extends AbstractCrudService<AwardProgramme, Long> {

    private final AwardDao awardDao;
    private final ApplicationEventPublisher eventPublisher;

    public AwardService(AwardDao awardDao, ApplicationEventPublisher eventPublisher) {
        this.awardDao = awardDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected GenericDao<AwardProgramme, Long> getDao() {
        return awardDao;
    }

    /** Hook override: brand-new programmes always start as DRAFT. */
    @Override
    protected void beforeSave(AwardProgramme programme) {
        if (programme.getId() == null && programme.getStatus() == null) {
            programme.setStatus(AwardStatus.DRAFT);
        }
    }

    /**
     * Observer pattern: when an existing programme's status changes, publish an
     * event. Listeners (for example the content module) react to it, and this
     * service does not need to know who they are.
     */
    @Override
    public AwardProgramme save(AwardProgramme programme) {
        AwardStatus oldStatus = programme.getId() == null
                ? null
                : awardDao.findById(programme.getId()).map(AwardProgramme::getStatus).orElse(null);
        AwardProgramme saved = super.save(programme);
        if (oldStatus != null && saved.getStatus() != oldStatus) {
            eventPublisher.publishEvent(new AwardStatusChangedEvent(saved, oldStatus));
        }
        return saved;
    }
}
