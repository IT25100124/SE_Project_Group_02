package com.sliit.awardvote.content.service;

import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.model.Banner;
import com.sliit.awardvote.content.model.Faq;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ContentFacade - Facade design pattern.
 *
 * Gives controllers one simple entry point to the three content services
 * (announcements, banners, FAQs) so they don't each have to know all three.
 */
@Component
public class ContentFacade {

    /** The bundle of content shown together on a page. */
    public record ContentBundle(List<Announcement> announcements, List<Banner> banners, List<Faq> faqs) {
    }

    private final AnnouncementService announcementService;
    private final BannerService bannerService;
    private final FaqService faqService;

    public ContentFacade(AnnouncementService announcementService, BannerService bannerService, FaqService faqService) {
        this.announcementService = announcementService;
        this.bannerService = bannerService;
        this.faqService = faqService;
    }

    /** Public site content: only active banners are included. */
    public ContentBundle publicContent() {
        return new ContentBundle(announcementService.findAll(), bannerService.findActive(), faqService.findAll());
    }

    /** Admin management content: every banner is included. */
    public ContentBundle allContent() {
        return new ContentBundle(announcementService.findAll(), bannerService.findAll(), faqService.findAll());
    }
}
