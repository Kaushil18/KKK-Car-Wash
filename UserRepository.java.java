package com.kkkarwash.repository;

import com.kkkarwash.model.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByStatus(User.Status status);
    long countByRole(User.Role role);
    long countByStatus(User.Status status);
}