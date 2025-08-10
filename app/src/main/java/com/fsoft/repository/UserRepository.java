package com.fsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fsoft.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
  User findByUsername(String username);

  User findByEmail(String email);

  User findById(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
  

  @Modifying
  @Query("UPDATE User u " +
      "SET u.verifyToken = null, u.isActive = true " +
      "WHERE u.username = :username")
  int verifyAccount(@Param("username") String username);
}
