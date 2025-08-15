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
public class InvitationNotificationDto {
    private UUID id;
    private UUID boardId;
    private String boardTitle;
    private String inviterUsername;
    private String inviterAvatar;
    private LocalDateTime sentAt;
    private boolean isRead;
}
