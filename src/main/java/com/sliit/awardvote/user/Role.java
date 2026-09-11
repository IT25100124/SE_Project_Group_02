package com.sliit.awardvote.user;

import com.sliit.awardvote.common.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * MODULE 1: USER MANAGEMENT — Roles & Permissions
 *
 * A Role is an administrator-defined, named bundle of {@link Permission}s
 * (e.g. "Regional Coordinator" -> REVIEW_NOMINATIONS + VOTE). This is
 * separate from the fixed {@link UserRole} enum, which still provides each
 * account's primary category (Public User, Judge, Awards Staff, ...) and a
 * sensible set of default permissions out of the box (see
 * {@link DefaultRolePermissions}) used whenever no custom Role is assigned.
 * Once a Role IS assigned to a User, it takes over completely — the user
 * gets exactly the permissions this Role carries, not the UserRole's
 * defaults plus this Role's (see User#hasPermission). A custom Role fully
 * defines what that person can do, rather than layering on top of something
 * else - so include everything they need, even things their base UserRole
 * would normally have granted for free.
 */
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<Permission> permissions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
