package com.fsoft.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class BoardDto {
  private UUID id;
  private String title;
  private String description;
  private LocalDate createdAt;
  private UserDto owner;
  private UserDto user;
}
