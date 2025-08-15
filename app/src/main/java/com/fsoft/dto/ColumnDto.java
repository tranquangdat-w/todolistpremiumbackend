package com.fsoft.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ColumnDto {
    private UUID id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private BigDecimal position;
    private UUID boardId;
}
