package com.sliit.awardvote.user.model;

import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * A single-use, time-limited one-time code, issued for one specific
 * {@link OtpPurpose} — new-account verification or a forgotten-password
 * reset. Both flows share this one mechanism (generate, dispatch via the
 * existing Email/SMS NotificationStrategy infrastructure, verify) instead of
 * each maintaining its own.
 *
 * Deliberately has no setter for the code/user/purpose/expiry once created —
 * only {@link #markUsed()} may change its state — matching the same
 * audit-integrity pattern used for Vote/Score in Nominee Management.
 */
@Entity
@Table(name = "otp_codes")
public class OtpCode extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    protected OtpCode() {
        // required by JPA
    }

    public OtpCode(User user, String code, OtpPurpose purpose, LocalDateTime expiresAt) {
        this.user = user;
        this.code = code;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
    }

    /** True if this code matches, hasn't been used yet, and hasn't expired. */
    public boolean isValid(String candidateCode) {
        return !used && LocalDateTime.now().isBefore(expiresAt) && code.equals(candidateCode);
    }

    public void markUsed() {
        this.used = true;
    }

    public User getUser() {
        return user;
    }

    public String getCode() {
        return code;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }
}
