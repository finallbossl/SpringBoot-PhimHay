package com.phimhay.juanng.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.phimhay.juanng.common.utils.UlidHelper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }

    @Column(unique = true, nullable = false)
    @NotBlank(message = "UserName không được để trống.")
    @Size(min = 1, max = 25, message = "UserName phải từ 4 đến 25 ký tự.")
    private String username;


    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    @Email(message = "Email không hợp lệ.")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "is_email_verified", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(name = "is_premium", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isPremium = false;

    @Column(name = "premium_expired_at")
    private LocalDateTime premiumExpiredAt;

    @Column(name = "provider", length = 20)
    @Builder.Default
    private String provider = "LOCAL"; // LOCAL, GOOGLE, FACEBOOK

    @Column(name = "provider_id")
    private String providerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user" ,cascade = CascadeType.ALL)
    private UserProfiles profiles;
}
