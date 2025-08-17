package com.fsoft.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fsoft.model.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDto {
    private UUID invitationId;
    private UUID boardId;
    private String boardTitle;
    private String inviterUsername;
    private String inviterAvatar;
    private String invitedUsername;
    private String invitedAvatar; // sender's avatar
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
}
