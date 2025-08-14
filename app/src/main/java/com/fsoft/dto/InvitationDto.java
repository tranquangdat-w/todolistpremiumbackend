package com.fsoft.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDto {
    private UUID id;
    private String boardTitle;
    private String inviterUsername;
    private String inviterAvatar;
    private String invitedUsername; // sender's username
    private String invitedAvatar; // sender's avatar
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
}
