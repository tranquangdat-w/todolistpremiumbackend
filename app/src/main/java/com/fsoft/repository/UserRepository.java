package com.fsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsoft.model.User;

public interface UserRepository extends JpaRepository<User, String> {
  User findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

}
