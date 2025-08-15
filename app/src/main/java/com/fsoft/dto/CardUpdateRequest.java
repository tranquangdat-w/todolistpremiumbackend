package com.fsoft.dto;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardUpdateRequest {
  private String title;

  private String description;

  private BigDecimal position;

  private Boolean isDone;

  private Instant deadline;

  @UUID
  private String columnId;

  @NotNull
  @UUID
  private String boardId;

  @AssertTrue(message = "Title must not be empty")
  public boolean isTitleValid() {
    return title == null || !title.isEmpty();
  }
}
