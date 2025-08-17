package com.fsoft.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

@Data
public class BoardDetailsDto {
  private UUID id;
  private String title;
  private String description;
  private UserDto owner;
  private Set<UserDto> members;
  private List<ColumnDetailsDto> columns;
  private Instant createdAt;
}
