package com.phimhay.juanng.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;

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

    @Builder.Default
    private boolean isActive = true;

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
