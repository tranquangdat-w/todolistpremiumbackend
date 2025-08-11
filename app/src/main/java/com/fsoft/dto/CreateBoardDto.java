package com.fsoft.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoardDto {
  @NotNull(message = "Board title can not be null")
  @NotEmpty(message = "Board title can not be empty")
  private String title;

  private String description;

  public void setTitle(String title) {
    this.title = title == null ? null : title.trim();
  }
}
