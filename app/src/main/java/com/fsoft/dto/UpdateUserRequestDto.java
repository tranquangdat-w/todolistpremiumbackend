package com.fsoft.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
  private String name;

  // Trim name before valid
  public void setName(String name) {
    this.name = name == null ? null : name.trim();
  }

  @AssertTrue(message = "Your name must not be empty")
  public boolean isTitleValid() {
    return name == null || !name.isEmpty();
  }
}
