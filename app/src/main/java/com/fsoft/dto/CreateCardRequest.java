package com.fsoft.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardRequest {
  @NotBlank(message = "Card title is required")
  private String title;

  @NotNull(message = "Column ID is required")
  @UUID
  private String columnId;

  @NotNull(message = "Board ID is required")
  @UUID
  private String boardId;

  @NotNull
  private BigDecimal position;
}
