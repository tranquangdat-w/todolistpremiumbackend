package com.fsoft.dto;

import java.util.UUID;

import com.fsoft.model.UserRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
  private UUID id;
  private String name;
  private String username;
  private String email;
  private UserRole userRole;
  private boolean isActive;
}
