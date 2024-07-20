package com.security.auth.repository;

import com.security.auth.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByUsername(String username);
    Optional<User> findFirstByProviderId(String providerId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
