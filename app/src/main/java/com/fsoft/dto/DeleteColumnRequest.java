package com.fsoft.dto;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteColumnRequest {
  @NotNull
  @UUID
  private String boardId;
}
