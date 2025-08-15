package com.fsoft.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectedNotificationDto {
    private UUID id;
    private UUID boardId;
    private String boardTitle;
    private String invitedUsername;
    private String invitedAvatar;
    private LocalDateTime respondedAt;
    private boolean read;
}
