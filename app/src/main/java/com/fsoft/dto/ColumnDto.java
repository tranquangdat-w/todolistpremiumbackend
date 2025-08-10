package com.fsoft.dto;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ColumnDto {
    private UUID id;
    private String title;
    private String description;
    private Date createdAt;
    private String comment;
    private UUID boardId;
}
