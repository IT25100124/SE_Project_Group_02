package com.sliit.awardvote.user;

import com.sliit.awardvote.common.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * MODULE 1: USER MANAGEMENT
 * Presented by: Nawarathna H.M.L.C. (IT25100124)
 *
 * Manages accounts, roles and access permissions for everyone interacting
 * with the system, from public voters to administrators.
 *
 * OOP concepts demonstrated:
 *  - INHERITANCE: User extends Person extends BaseEntity (3-level chain)
 *  - POLYMORPHISM: overrides Person#getRoleDescription()
 *  - ENCAPSULATION: all fields private, accessed only via getters/setters
 *
 * Roles & Permissions: every user has a fixed {@link UserRole} category
 * (Public User, Judge, Awards Staff, ...) which carries sensible default
 * permissions (see {@link DefaultRolePermissions}) used whenever no custom
 * role is assigned. Once an administrator assigns an admin-created
 * {@link Role}, it takes over completely — the user gets EXACTLY the
 * permissions checked on that role, not the base role's defaults plus the
 * custom role's. {@link #hasPermission(Permission)} is the single source of
 * truth every controller checks against.
 */
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

    /** Polymorphic override - behaviour differs per role at runtime. */
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

    /**
     * True if this user may perform the given capability.
     *
     * - SYSTEM_ADMIN always has every permission, even if a custom role was
     *   mistakenly assigned to them - this is a deliberate safety net so an
     *   admin account can never accidentally lock itself out.
     * - If a custom role IS assigned (any other account), it is the SOLE
     *   authority: the user gets exactly the permissions checked on that
     *   role, and nothing from their base UserRole's defaults. Select every
     *   permission the person needs, including ones like VOTE if they
     *   should keep that too.
     * - If no custom role is assigned, the base UserRole's defaults apply
     *   (see DefaultRolePermissions) - this is what makes every built-in
     *   role work out of the box with zero configuration.
     */
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

