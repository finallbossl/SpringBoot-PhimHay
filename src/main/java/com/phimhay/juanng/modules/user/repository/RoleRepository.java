package com.phimhay.juanng.modules.user.repository;

import com.phimhay.juanng.modules.user.entity.Role;
import com.phimhay.juanng.modules.user.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRole(RoleType role);
}
