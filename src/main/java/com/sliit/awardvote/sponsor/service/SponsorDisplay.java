package com.sliit.awardvote.sponsor.service;

import com.sliit.awardvote.sponsor.model.Sponsor;

/** A sponsor together with the display rules chosen by its tier strategy (used by the page templates). */
public class SponsorDisplay {

    private final Sponsor sponsor;
    private final String iconClass;
    private final String benefits;

    public SponsorDisplay(Sponsor sponsor, String iconClass, String benefits) {
        this.sponsor = sponsor;
        this.iconClass = iconClass;
        this.benefits = benefits;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getBenefits() {
        return benefits;
    }
}
