package com.fsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationDto {
    private UUID id;
    private UUID boardId;
    private UUID cardId;
    private String cardTitle;
    private String boardTitle;
    private String commenterUsername;
    private String commenterAvatar;
    private LocalDateTime commentedAt;
    private boolean isRead;
}
