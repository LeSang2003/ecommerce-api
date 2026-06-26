package com.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false
    )
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // =========================
    // EMAIL VERIFY
    // =========================

    @Column(name = "enabled")
    private Boolean enabled = false;

    // =========================
    // BAN
    // =========================

    @Column(name = "banned")
    private Boolean banned = false;

    // =========================
    // REVIEWS
    // =========================

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Review> reviews;

    // =========================
    // DEFAULT VALUES
    // =========================

    @PrePersist
    public void prePersist() {

        if (banned == null) {
            banned = false;
        }

        if (enabled == null) {
            enabled = false;
        }
    }

    // =========================
    // USER DETAILS
    // =========================

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // =========================
    // LOGIN CHECK
    // =========================

    @Override
    public boolean isEnabled() {

        // chưa verify email
        if (enabled == null || !enabled) {
            return false;
        }

        // bị ban
        return banned == null || !banned;
    }
    private String fullName;

    private String phone;

    private String address;

    private LocalDate birthday;

    private String gender;

    private String avatar;
}