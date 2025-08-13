package com.fsoft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private List<InvitationNotificationDto> invitation;
    private List<AcceptedNotificationDto> accepted;
    private List<RejectedNotificationDto> rejected;
    private List<CommentNotificationDto> comment;
}
