package com.sliit.awardvote.sponsor.util;

import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.model.SponsorTier;

/**
 * SponsorBuilder - Builder design pattern.
 *
 * Builds a Sponsor step by step with readable, chained calls instead of many
 * separate setter lines. Only the name is required; everything else is optional.
 */
public class SponsorBuilder {

    private final Sponsor sponsor = new Sponsor();

    public SponsorBuilder(String name) {
        sponsor.setName(name);
    }

    public SponsorBuilder contactPerson(String contactPerson) {
        sponsor.setContactPerson(contactPerson);
        return this;
    }

    public SponsorBuilder email(String email) {
        sponsor.setEmail(email);
        return this;
    }

    public SponsorBuilder phone(String phone) {
        sponsor.setPhone(phone);
        return this;
    }

    public SponsorBuilder logoUrl(String logoUrl) {
        sponsor.setLogoUrl(logoUrl);
        return this;
    }

    public SponsorBuilder tier(SponsorTier tier) {
        sponsor.setTier(tier);
        return this;
    }

    public SponsorBuilder description(String description) {
        sponsor.setDescription(description);
        return this;
    }

    public Sponsor build() {
        return sponsor;
    }
}
