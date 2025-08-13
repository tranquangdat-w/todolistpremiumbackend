package com.fsoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardUploadDto {
    @NotNull(message = "Task title cannot be null")
    private String taskTitle;
    @NotNull(message = "Task description cannot be null")
    private String description;
    private Boolean isDone;
    @NotNull(message = "Created date cannot be null")
    private Date createdAt;
    @NotNull(message = "Deadline cannot be null")
    private Date deadline;
    private String columnId;
}
