package com.fsoft.dto;

import java.util.UUID;

import com.fsoft.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
  private UUID id;
  private String name;
  private String username;
  private String email;
  private UserRole userRole;
  private boolean active;
  private String avatar;

}
