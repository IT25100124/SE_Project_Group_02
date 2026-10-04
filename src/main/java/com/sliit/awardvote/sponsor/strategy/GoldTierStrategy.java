package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Gold: shown after Platinum with a large icon. */
@Component
public class GoldTierStrategy implements SponsorTierStrategy {

    @Override
    public SponsorTier tier() {
        return SponsorTier.GOLD;
    }

    @Override
    public int displayPriority() {
        return 2;
    }

    @Override
    public String iconClass() {
        return "fs-2";
    }

    @Override
    public String benefits() {
        return "High placement, large icon";
    }
}
