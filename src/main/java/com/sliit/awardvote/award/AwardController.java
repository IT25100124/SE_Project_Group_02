package com.sliit.awardvote.award;

import com.sliit.awardvote.common.SessionUtil;
import com.sliit.awardvote.nominee.Nomination;
import com.sliit.awardvote.nominee.NominationService;
import com.sliit.awardvote.user.Permission;
import com.sliit.awardvote.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AwardController {

    private final AwardService awardService;
    private final CategoryService categoryService;
    private final NominationService nominationService;

    public AwardController(AwardService awardService, CategoryService categoryService, NominationService nominationService) {
        this.awardService = awardService;
        this.categoryService = categoryService;
        this.nominationService = nominationService;
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
        model.addAttribute("programme", new AwardProgramme());
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
    public String save(@ModelAttribute AwardProgramme programme, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
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
                nominationService.findVoteInCategory(category.getId(), current.getId())
                        .ifPresent(v -> myVotes.put(category.getId(), v.getNomination().getId()));
            }
            model.addAttribute("myVotes", myVotes);
        }
        if (current != null && current.hasPermission(Permission.JUDGE_NOMINATIONS)) {
            Map<Long, Long> myJudgments = new HashMap<>();
            for (Category category : categories) {
                nominationService.findScoreInCategory(category.getId(), current.getId())
                        .ifPresent(s -> myJudgments.put(category.getId(), s.getNomination().getId()));
            }
            model.addAttribute("myJudgments", myJudgments);
        }
        return "awards/view";
    }

    // ---------- Categories ----------

    @GetMapping("/awards/{programmeId}/categories/new")
    public String newCategoryForm(@PathVariable Long programmeId, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        Category category = new Category();
        category.setAwardProgramme(awardService.findById(programmeId).orElseThrow());
        model.addAttribute("category", category);
        return "categories/form";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        model.addAttribute("category", categoryService.findById(id).orElseThrow());
        return "categories/form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category,
                                @RequestParam Long programmeId,
                                HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        category.setAwardProgramme(awardService.findById(programmeId).orElseThrow());
        categoryService.save(category);
        return "redirect:/awards/" + programmeId;
    }

    @GetMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        if (!canManage(session)) return "redirect:/awards";
        Category category = categoryService.findById(id).orElseThrow();
        Long programmeId = category.getAwardProgramme().getId();
        categoryService.deleteById(id);
        return "redirect:/awards/" + programmeId;
    }
}
