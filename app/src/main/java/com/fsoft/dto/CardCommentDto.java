package com.fsoft.dto;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardCommentDto {
    private UUID id;
    private UUID cardId;
    private UUID userId;
    private String userAvatarUrl;
    private String content;
    private Date createdAt;
}
