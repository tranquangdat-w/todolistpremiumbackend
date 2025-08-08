package com.fsoft.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "name")
  @NotNull
  private String name;

  @Column(name = "user_name", unique = true)
  @NotNull
  private String username;

  @Column(name = "password")
  @NotNull
  private String password;

  @Column(name = "email")
  @NotNull
  private String email;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole userRole = UserRole.CLIENT;

  @Column(name = "is_active")
  @NotNull
  private boolean isActive;
}
