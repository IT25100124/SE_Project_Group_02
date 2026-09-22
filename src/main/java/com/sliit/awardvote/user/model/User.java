package com.sliit.awardvote.user.model;

import com.sliit.awardvote.common.model.Person;
import com.sliit.awardvote.user.util.DefaultRolePermissions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User extends Person {

    @Column(unique = true, nullable = false)
    private String username;

    /** Stored as a SHA-256 hash, never in plain text - see PasswordUtil. */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean active = true;

    /** Optional admin-assigned custom role. When set, it exclusively determines permissions - see hasPermission(). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custom_role_id")
    private Role customRole;

    public User() {
    }

    public User(String fullName, String email, String username, String password, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getRoleDescription() {
        return switch (role) {
            case SYSTEM_ADMIN -> "Manages user accounts, award categories, voting periods and system settings.";
            case AWARDS_STAFF -> "Reviews and approves nominations and runs the day-to-day award process.";
            case JUDGE -> "Evaluates approved nominees against defined judging criteria.";
            case MARKETING_OFFICER -> "Manages notifications, announcements and website content.";
            case SPONSOR_COORDINATOR -> "Manages sponsor and partner profiles and communications.";
            case NOMINEE -> "Views their own nomination details and tracks its status.";
            case PUBLIC_USER -> "Views award categories, submits nominations and votes where permitted.";
        };
    }

    public boolean hasPermission(Permission permission) {
        if (role == UserRole.SYSTEM_ADMIN) {
            return true;
        }
        if (customRole != null) {
            return customRole.hasPermission(permission);
        }
        return DefaultRolePermissions.forRole(role).contains(permission);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getCustomRole() {
        return customRole;
    }

    public void setCustomRole(Role customRole) {
        this.customRole = customRole;
    }
}

