package com.sliit.awardvote.sponsor.strategy;

import com.sliit.awardvote.sponsor.model.SponsorTier;

/**
 * SponsorTierStrategy - Strategy design pattern.
 *
 * Each sponsor tier (Platinum, Gold, Silver, Bronze) has its own display
 * rules: where it is placed, how big its icon is and what it gets.
 * SponsorService picks the matching strategy at runtime from the sponsor's tier.
 */
public interface SponsorTierStrategy {

    /** The tier this strategy handles. */
    SponsorTier tier();

    /** Placement order on the page (1 = shown first). */
    int displayPriority();

    /** Bootstrap font-size class for the sponsor icon (fs-1 is the largest). */
    String iconClass();

    /** Short text describing the display benefit of the tier. */
    String benefits();
}
