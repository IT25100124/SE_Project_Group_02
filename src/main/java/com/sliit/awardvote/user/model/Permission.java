package com.sliit.awardvote.user.model;

/**
 * MODULE 1: USER MANAGEMENT — Roles & Permissions
 *
 * The fine-grained capabilities that a Role (custom or built-in) can grant.
 * This is the vocabulary every permission check in the system is written
 * against, so adding a new capability anywhere in the app means adding one
 * value here and referencing it — no scattered role-name string comparisons.
 */
public enum Permission {
    MANAGE_USERS("Create, edit, deactivate and assign roles to user accounts"),
    MANAGE_ROLES("Create and edit custom roles and their permissions"),
    MANAGE_AWARDS("Create and edit award programmes and categories"),
    MANAGE_NOMINEES("Add, edit and delete nominee entries within award categories"),
    REVIEW_NOMINATIONS("Approve or reject submitted nominations"),
    JUDGE_NOMINATIONS("Submit judge evaluation scores for approved nominees"),
    MANAGE_SPONSORS("Create and edit sponsor and partner profiles"),
    MANAGE_CONTENT("Publish announcements, banners and FAQs"),
    MANAGE_NOTIFICATIONS("Create, edit, delete and view every notification sent to any user"),
    HANDLE_FEEDBACK("Create, edit, delete and respond to public feedback and inquiries"),
    VOTE("Cast a public vote for an approved nominee");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
