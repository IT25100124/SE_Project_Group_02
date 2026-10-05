package com.sliit.awardvote.award.controller;

//Model class
import com.sliit.awardvote.notification.model.AwardFeedback;
import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.notification.service.AwardFeedbackService;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.nominee.model.Nomination;
import com.sliit.awardvote.nominee.service.NominationService;
import com.sliit.awardvote.nominee.service.ScoreService;
import com.sliit.awardvote.nominee.service.VoteService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AwardController { // Handle the http reqst and send the service layer

    /** Awards can only be created for this year or later. */
    private static final int MIN_YEAR = 2026;

    private final AwardService awardService;
    private final CategoryService categoryService;
    private final NominationService nominationService;
    private final VoteService voteService;
    private final ScoreService scoreService;
    private final AwardFeedbackService awardFeedbackService;

    public AwardController(AwardService awardService, CategoryService categoryService, NominationService nominationService,
                           VoteService voteService, ScoreService scoreService, AwardFeedbackService awardFeedbackService) {
        this.awardService = awardService;
        this.categoryService = categoryService;
        this.nominationService = nominationService;
        this.voteService = voteService;
        this.scoreService = scoreService;
        this.awardFeedbackService = awardFeedbackService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_AWARDS);
    }

    // ---------- Award programmes ----------

    @GetMapping("/awards")
    public String list(Model model) {
        model.addAttribute("programmes", awardService.findAll());
        return "awards/list";
    }

    @GetMapping("/awards/new")
    public String newForm(Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        AwardProgramme programme = new AwardProgramme();
        programme.setYear(MIN_YEAR);
        model.addAttribute("programme", programme);
        model.addAttribute("statuses", AwardStatus.values());
        return "awards/form";
    }

    @GetMapping("/awards/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        model.addAttribute("programme", awardService.findById(id).orElseThrow());
        model.addAttribute("statuses", AwardStatus.values());
        return "awards/form";
    }

    @PostMapping("/awards/save")
    public String save(@ModelAttribute AwardProgramme programme, HttpSession session, Model model) {
        if (!canManage(session)) return "redirect:/awards";
        if (programme.getYear() < MIN_YEAR) {
            model.addAttribute("statuses", AwardStatus.values());
            model.addAttribute("yearError", "Year must be " + MIN_YEAR + " or later.");
            return "awards/form";
        }
        awardService.save(programme);
        return "redirect:/awards";
    }

    @GetMapping("/awards/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        awardService.deleteById(id);
        return "redirect:/awards";
    }

    @GetMapping("/awards/{id}")
    public String view(@PathVariable Long id, Model model, HttpSession session) {
        AwardProgramme programme = awardService.findById(id).orElseThrow();
        List<Category> categories = categoryService.findByProgramme(id);
        model.addAttribute("programme", programme);
        model.addAttribute("categories", categories);

        Map<Long, List<Nomination>> approvedByCategory = new HashMap<>();
        for (Category category : categories) {
            approvedByCategory.put(category.getId(), nominationService.findApprovedByCategory(category.getId()));
        }
        model.addAttribute("approvedByCategory", approvedByCategory);

        User current = SessionUtil.currentUser(session);
        if (current != null && current.hasPermission(Permission.VOTE)) {
            Map<Long, Long> myVotes = new HashMap<>();
            for (Category category : categories) {
                voteService.findVoteInCategory(category.getId(), current.getId())
                        .ifPresent(v -> myVotes.put(category.getId(), v.getNomination().getId()));
            }
            model.addAttribute("myVotes", myVotes);
        }
        if (current != null && current.hasPermission(Permission.JUDGE_NOMINATIONS)) {
            Map<Long, Long> myJudgments = new HashMap<>();
            for (Category category : categories) {
                scoreService.findScoreInCategory(category.getId(), current.getId())
                        .ifPresent(s -> myJudgments.put(category.getId(), s.getNomination().getId()));
            }
            model.addAttribute("myJudgments", myJudgments);
        }

        List<AwardFeedback> awardFeedback = awardFeedbackService.findByProgramme(id);
        model.addAttribute("awardFeedback", awardFeedback);
        model.addAttribute("currentUserId", current != null ? current.getId() : null);

        return "awards/view";
    }
}
