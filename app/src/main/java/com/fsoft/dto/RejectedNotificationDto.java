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
public class RejectedNotificationDto {
    private UUID id;
    private UUID boardId;
    private String boardTitle;
    private String invitedUsername;
    private String invitedAvatar;
    private LocalDateTime respondedAt;
    private boolean isRead;
}
