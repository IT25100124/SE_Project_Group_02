package com.sliit.awardvote.award;

import com.sliit.awardvote.common.AbstractCrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AwardService extends AbstractCrudService<AwardProgramme, Long> {

    private final AwardRepository awardRepository;

    public AwardService(AwardRepository awardRepository) {
        this.awardRepository = awardRepository;
    }

    @Override
    protected JpaRepository<AwardProgramme, Long> getRepository() {
        return awardRepository;
    }

    /** Hook override: brand-new programmes always start as DRAFT. */
    @Override
    protected void beforeSave(AwardProgramme programme) {
        if (programme.getId() == null && programme.getStatus() == null) {
            programme.setStatus(AwardStatus.DRAFT);
        }
    }
}
