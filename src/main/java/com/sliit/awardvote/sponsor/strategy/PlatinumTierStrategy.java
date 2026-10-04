package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Platinum: shown first with the largest icon. */
@Component
public class PlatinumTierStrategy implements SponsorTierStrategy {

    @Override
    public SponsorTier tier() {
        return SponsorTier.PLATINUM;
    }

    @Override
    public int displayPriority() {
        return 1;
    }

    @Override
    public String iconClass() {
        return "fs-1";
    }

    @Override
    public String benefits() {
        return "Top placement, largest icon";
    }
}
