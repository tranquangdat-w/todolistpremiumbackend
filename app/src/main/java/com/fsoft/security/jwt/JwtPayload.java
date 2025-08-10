package com.fsoft.security.jwt;

import com.fsoft.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class JwtPayload {
  private UUID id;
  private String username;
  private String email;
  private UserRole userRole;
  private boolean active;
  private LocalDate createdAt;
}
