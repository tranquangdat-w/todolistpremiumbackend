package com.fsoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ColumnRegistrationRequest {
    @NotNull(message = "Column title cannot be empty")
    private String title;

    private String description;

    private Date createdAt;

    private String comment;

    private UUID boardId;
}
