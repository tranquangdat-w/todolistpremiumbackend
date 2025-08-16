package com.fsoft.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateCardCommentDto {
    private UUID cardId;
    private String content;
}
