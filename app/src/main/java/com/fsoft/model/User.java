package com.fsoft.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "user_name", unique = true)
  private String username;

  @NotNull
  @Column(name = "password")
  private String password;

  @NotNull
  @Column(name = "email")
  private String email;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole userRole = UserRole.CLIENT;

  @NotNull
  @Column(name = "is_active")
  private boolean active;

  @Column(name = "verify_token")
  private String verifyToken;

  @Column(name = "avatar", columnDefinition = "text")
  private String avatar;

  @NotNull
  @Column(name = "created_at")
  private LocalDate createdAt;

  @ManyToMany(mappedBy = "members")
  private Set<Board> boards;

  @OneToMany(mappedBy = "user")
  private List<CardMember> cardMembers;
}
