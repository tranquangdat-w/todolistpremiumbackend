package com.fsoft.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBoardDto {
  private String title;
  private String description;

  public void setTitle(String title) {
    this.title = title == null ? null : title.trim();
  }

  @AssertTrue(message = "Title must not be empty if provided")
  public boolean isTitleValid() {
    return title == null || !title.isEmpty();
  }
}
