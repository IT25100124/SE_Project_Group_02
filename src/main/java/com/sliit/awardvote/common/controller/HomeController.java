package com.sliit.awardvote.common.controller;

import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.content.service.ContentFacade;
import com.sliit.awardvote.sponsor.service.SponsorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** The public landing page. */
@Controller
public class HomeController {

    private final AwardService awardService;
    private final SponsorService sponsorService;
    private final ContentFacade contentFacade;

    public HomeController(AwardService awardService, SponsorService sponsorService, ContentFacade contentFacade) {
        this.awardService = awardService;
        this.sponsorService = sponsorService;
        this.contentFacade = contentFacade;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("programmes", awardService.findAll());
        model.addAttribute("sponsors", sponsorService.findAll());
        var content = contentFacade.publicContent();
        model.addAttribute("announcements", content.announcements());
        model.addAttribute("banners", content.banners());
        model.addAttribute("faqs", content.faqs());
        return "index";
    }
}
