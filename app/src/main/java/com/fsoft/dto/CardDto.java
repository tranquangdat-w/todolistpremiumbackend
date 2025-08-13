package com.fsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private UUID cardID;
    private String taskTitle;
    private String description;
    private Boolean isDone;
    private Date createdAt;
    private Date deadline;
    private String columnId;
}
