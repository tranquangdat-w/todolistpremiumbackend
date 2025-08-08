package com.fsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fsoft.model.User;

/**
 * Created on Ağustos, 2020
 *
 * @author Faruk
 */
public interface UserRepository extends JpaRepository<User, String> {
  User findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

}
