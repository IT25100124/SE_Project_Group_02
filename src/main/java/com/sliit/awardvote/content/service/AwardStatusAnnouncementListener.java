package com.sliit.awardvote.content.service;

import com.sliit.awardvote.award.event.AwardStatusChangedEvent;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.content.model.Announcement;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Observer design pattern: listens for award status changes and posts a public
 * announcement. AwardService only publishes the event and does not know this
 * listener exists, so more listeners can be added without changing it.
 */
@Component
public class AwardStatusAnnouncementListener {

    private final AnnouncementService announcementService;

    public AwardStatusAnnouncementListener(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @EventListener
    public void onAwardStatusChanged(AwardStatusChangedEvent event) {
        AwardProgramme programme = event.programme();
        String name = programme.getName();

        String title;
        String content;
        switch (event.newStatus()) {
            case OPEN:
                title = "Nominations are now open!";
                content = "Submit your nominations for " + name + " today.";
                break;
            case CLOSED:
                title = "Voting has closed";
                content = "Voting for " + name + " has now closed. Thank you for taking part.";
                break;
            case COMPLETED:
                title = name + " is complete";
                content = "The " + name + " season has finished. Thank you to everyone who took part.";
                break;
            default:
                return; // no announcement for DRAFT
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setActive(true);
        announcement.setPublishDate(LocalDateTime.now());
        announcementService.save(announcement);
    }
}
