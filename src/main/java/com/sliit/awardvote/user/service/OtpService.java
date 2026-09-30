package com.sliit.awardvote.user.service;

import com.sliit.awardvote.notification.model.NotificationType;
import com.sliit.awardvote.notification.service.NotificationService;
import com.sliit.awardvote.user.model.OtpCode;
import com.sliit.awardvote.user.model.OtpPurpose;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.dao.OtpCodeDao;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Generates, dispatches and verifies {@link OtpCode}s. Used by both
 * {@code RegistrationService} (ACCOUNT_VERIFICATION) and
 * {@code PasswordResetService} (PASSWORD_RESET) so there is exactly one
 * place that knows how a code is generated, sent, and checked.
 *
 * Delivery reuses the existing {@code NotificationStrategy} (Email/SMS)
 * infrastructure from Module 4 — no separate dispatch mechanism.
 */
@Service
public class OtpService {

    private static final int OTP_VALID_MINUTES = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpCodeDao otpCodeDao;
    private final NotificationService notificationService;

    public OtpService(OtpCodeDao otpCodeDao, NotificationService notificationService) {
        this.otpCodeDao = otpCodeDao;
        this.notificationService = notificationService;
    }

    /**
     * Invalidates any earlier unused codes of the same purpose for this user,
     * issues a fresh 6-digit code valid for 10 minutes, and sends it. SMS
     * silently falls back to email if the account has no phone number on file.
     */
    public void issueAndSend(User user, OtpPurpose purpose, NotificationType preferredChannel, String messageText) {
        otpCodeDao.findByUserIdAndPurposeAndUsedFalse(user.getId(), purpose).forEach(otp -> {
            otp.markUsed();
            otpCodeDao.save(otp);
        });

        String code = generateSixDigitCode();
        otpCodeDao.save(new OtpCode(user, code, purpose, LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES)));

        NotificationType channel = preferredChannel;
        if (channel == NotificationType.SMS && (user.getPhone() == null || user.getPhone().isBlank())) {
            channel = NotificationType.EMAIL;
        }

        notificationService.notify(user, messageText.replace("{code}", code).replace("{minutes}", String.valueOf(OTP_VALID_MINUTES)),
                channel, purpose.name());
    }

    /** Verifies a code for the given user and purpose; marks it used if valid. Returns false on any mismatch or expiry. */
    public boolean verify(User user, String code, OtpPurpose purpose) {
        return otpCodeDao.findByUserIdAndPurposeAndUsedFalse(user.getId(), purpose).stream()
                .filter(otp -> otp.isValid(code))
                .findFirst()
                .map(otp -> {
                    otp.markUsed();
                    otpCodeDao.save(otp);
                    return true;
                })
                .orElse(false);
    }

    private String generateSixDigitCode() {
        int number = RANDOM.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
