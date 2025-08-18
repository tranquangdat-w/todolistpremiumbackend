package com.fsoft.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class BoardDto {
  private UUID id;
  private String title;
  private String description;
  private Instant createdAt;
}
