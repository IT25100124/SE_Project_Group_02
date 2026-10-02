package com.sliit.awardvote.award.service;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.dao.AwardDao;
import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.award.state.AwardStates;
import com.sliit.awardvote.common.service.AbstractCrudService;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AwardService extends AbstractCrudService<AwardProgramme, Long> {

    private final AwardDao awardDao;

    public AwardService(AwardDao awardDao) {
        this.awardDao = awardDao;
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
     * State pattern check: an existing programme may only move to a status its
     * current state allows (e.g. DRAFT -> OPEN, not DRAFT -> COMPLETED).
     * Returns an error message, or empty when the change is allowed.
     */
    public Optional<String> validateStatusChange(AwardProgramme edited) {
        if (edited.getId() == null || edited.getStatus() == null) {
            return Optional.empty(); // new programme
        }
        return awardDao.findById(edited.getId())
                .filter(existing -> existing.getStatus() != null)
                .filter(existing -> !AwardStates.of(existing.getStatus()).canChangeTo(edited.getStatus()))
                .map(existing -> "A " + existing.getStatus() + " programme cannot be changed to "
                        + edited.getStatus() + ".");
    }
}
