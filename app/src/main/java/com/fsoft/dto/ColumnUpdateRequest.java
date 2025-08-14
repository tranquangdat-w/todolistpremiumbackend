package com.fsoft.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColumnUpdateRequest {
  private String title;
  private BigDecimal position;
  @NotNull
  @UUID
  private String boardId;

  @AssertTrue(message = "Title must not be empty")
  public boolean isTitleValid() {
    return title == null || !title.isEmpty();
  }
}
