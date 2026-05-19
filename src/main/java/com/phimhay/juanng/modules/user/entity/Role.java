package com.phimhay.juanng.modules.user.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20,unique = true , nullable = false)
    private RoleType role;


}
