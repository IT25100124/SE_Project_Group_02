package com.sliit.awardvote.sponsor.controller;

import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.model.SponsorTier;
import com.sliit.awardvote.sponsor.service.SponsorService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SponsorController {

    private final SponsorService sponsorService;

    public SponsorController(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_SPONSORS);
    }

    @GetMapping("/sponsors")
    public String list(Model model) {
        model.addAttribute("sponsors", sponsorService.findAllForDisplay());
        return "sponsors/list";
    }

    @GetMapping("/sponsors/new")
    public String newForm(Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/sponsors";
        model.addAttribute("sponsor", new Sponsor());
        model.addAttribute("tiers", SponsorTier.values());
        return "sponsors/form";
    }

    @GetMapping("/sponsors/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/sponsors";
        model.addAttribute("sponsor", sponsorService.findById(id).orElseThrow());
        model.addAttribute("tiers", SponsorTier.values());
        return "sponsors/form";
    }

    @PostMapping("/sponsors/save")
    public String save(@ModelAttribute Sponsor sponsor, HttpSession session) {
        if (!canManage(session)) return "redirect:/sponsors";
        sponsorService.save(sponsor);
        return "redirect:/sponsors";
    }

    @GetMapping("/sponsors/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!canManage(session)) return "redirect:/sponsors";
        try {
            sponsorService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Sponsor is linked to at least one award programme - unlink it from every
            // programme first (edit each programme's sponsor list), then delete.
            redirectAttributes.addFlashAttribute("error",
                    "Can't delete this sponsor - it's still linked to at least one award programme. "
                            + "Remove it from that programme first, then delete it.");
        }
        return "redirect:/sponsors";
    }
}
