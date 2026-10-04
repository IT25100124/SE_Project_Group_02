package com.sliit.awardvote.award.controller;

import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.common.util.SessionUtil;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** Handles the Category screens (create / edit / delete) that belong to an award programme. */
@Controller
public class CategoryController {

    private final AwardService awardService;
    private final CategoryService categoryService;

    public CategoryController(AwardService awardService, CategoryService categoryService) {
        this.awardService = awardService;
        this.categoryService = categoryService;
    }

    private boolean canManage(HttpSession session) {
        User u = SessionUtil.currentUser(session);
        return u != null && u.hasPermission(Permission.MANAGE_AWARDS);
    }

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
