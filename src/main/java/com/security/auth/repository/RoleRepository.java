package com.security.auth.repository;

import com.security.auth.model.Role;
import com.security.auth.model.RoleEnum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
