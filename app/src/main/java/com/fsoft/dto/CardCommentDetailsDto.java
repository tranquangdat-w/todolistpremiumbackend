package com.fsoft.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class CardCommentDetailsDto {
  private UUID id;
  private UserDto user;
  private String content;
  private Instant createdAt;
}
