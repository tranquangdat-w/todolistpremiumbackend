package com.fsoft.dto;

import com.fsoft.model.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedUserDto {

  private String name;

  private String username;

  private String password;

  private UserRole userRole;
}
