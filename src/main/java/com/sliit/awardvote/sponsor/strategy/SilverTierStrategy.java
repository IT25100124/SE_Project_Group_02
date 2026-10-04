package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

import org.springframework.stereotype.Component;

/** Silver: standard placement with a medium icon. */
@Component
public class SilverTierStrategy implements SponsorTierStrategy {

    @Override
    public SponsorTier tier() {
        return SponsorTier.SILVER;
    }

    @Override
    public int displayPriority() {
        return 3;
    }

    @Override
    public String iconClass() {
        return "fs-3";
    }

    @Override
    public String benefits() {
        return "Standard placement, medium icon";
    }
}
