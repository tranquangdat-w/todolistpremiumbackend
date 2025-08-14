package com.fsoft.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ColumnRegistrationRequest {
  @NotNull(message = "Column title cannot be null")
  @NotEmpty(message = "Board title can not be empty")
  private String title;

  @NotNull(message = "boarId cannot be null")
  @UUID(message = "boardId is invalid")
  private String boardId;

  @NotNull
  private BigDecimal position;

  public void setTitle(String title) {
    this.title = title == null ? null : title.trim();
  }
}
