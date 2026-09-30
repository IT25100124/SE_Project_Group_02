package com.sliit.awardvote.sponsor.model;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 5: SPONSOR & PARTNER MANAGEMENT
 * Presented by: Chandrasooriya J.T. (IT25100146)
 *
 * Relationships: MANY-TO-MANY with AwardProgramme (inverse side).
 * Deliberately has NO access to Nomination/Vote/Score data - sponsors never
 * see confidential voting results, per the proposal's Restrictions (8.3).
 */
@Entity
@Table(name = "sponsors")
public class Sponsor extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String contactPerson;
    private String email;
    private String phone;
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    private SponsorTier tier = SponsorTier.BRONZE;

    @Column(length = 1000)
    private String description;

    @ManyToMany(mappedBy = "sponsors")
    private Set<AwardProgramme> programmes = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public SponsorTier getTier() {
        return tier;
    }

    public void setTier(SponsorTier tier) {
        this.tier = tier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AwardProgramme> getProgrammes() {
        return programmes;
    }

    public void setProgrammes(Set<AwardProgramme> programmes) {
        this.programmes = programmes;
    }
}
