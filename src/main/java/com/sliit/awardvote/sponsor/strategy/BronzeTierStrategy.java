package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Bronze: listed last with a small icon. */
@Component
public class BronzeTierStrategy implements SponsorTierStrategy {

    @Override
    public SponsorTier tier() {
        return SponsorTier.BRONZE;
    }

    @Override
    public int displayPriority() {
        return 4;
    }

    @Override
    public String iconClass() {
        return "fs-5";
    }

    @Override
    public String benefits() {
        return "Listed after higher tiers, small icon";
    }
}
