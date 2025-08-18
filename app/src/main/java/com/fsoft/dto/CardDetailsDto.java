package com.fsoft.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CardDetailsDto {
  private UUID id;
  private String title;
  private Boolean isDone;
  private Date createdAt;
  private Instant deadline;
  private BigDecimal position;
  private String cover;
  private UUID columnId;
  private String description;
  private List<CardCommentDetailsDto> comments;
  private List<UUID> memberIds;
}
