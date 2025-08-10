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
public class ColumnUpdateRequest {
    @NotNull(message = "Column id cannot be null")
    private String id;
    @NotNull(message = "Title cannot be null")
    private String title;
    private String description;
    private Date createdAt;
    private String comment;
}
