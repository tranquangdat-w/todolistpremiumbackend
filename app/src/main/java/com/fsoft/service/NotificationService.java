package com.fsoft.service;

import com.fsoft.dto.*;
import com.fsoft.model.Notification;
import com.fsoft.model.NotificationType;
import com.fsoft.model.User;
import com.fsoft.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public NotificationResponseDto getUserNotifications(UUID userId) {
        List<Notification> allNotifications = notificationRepository.findByUserId(userId);

        List<InvitationNotificationDto> invitationNotifications = new ArrayList<>();
        List<AcceptedNotificationDto> acceptedNotifications = new ArrayList<>();
        List<RejectedNotificationDto> rejectedNotifications = new ArrayList<>();
        List<CommentNotificationDto> commentNotifications = new ArrayList<>();

        for (Notification notification : allNotifications) {
            try {
                switch (notification.getType()) {
                    case INVITATION:
                        InvitationNotificationDto invitationDto = objectMapper.readValue(
                                notification.getData(), InvitationNotificationDto.class);
                        invitationDto.setId(notification.getId());
                        invitationDto.setRead(notification.getIsRead());
                        invitationNotifications.add(invitationDto);
                        break;

                    case ACCEPTED:
                        AcceptedNotificationDto acceptedDto = objectMapper.readValue(
                                notification.getData(), AcceptedNotificationDto.class);
                        acceptedDto.setId(notification.getId());
                        acceptedDto.setRead(notification.getIsRead());
                        acceptedNotifications.add(acceptedDto);
                        break;

                    case REJECTED:
                        RejectedNotificationDto rejectedDto = objectMapper.readValue(
                                notification.getData(), RejectedNotificationDto.class);
                        rejectedDto.setId(notification.getId());
                        rejectedDto.setRead(notification.getIsRead());
                        rejectedNotifications.add(rejectedDto);
                        break;

                    case COMMENT:
                        CommentNotificationDto commentDto = objectMapper.readValue(
                                notification.getData(), CommentNotificationDto.class);
                        commentDto.setId(notification.getId());
                        commentDto.setRead(notification.getIsRead());
                        commentNotifications.add(commentDto);
                        break;
                }
            } catch (Exception e) {
                // Log error parsing notification
                e.printStackTrace();
            }
        }

        return NotificationResponseDto.builder()
                .invitation(invitationNotifications)
                .accepted(acceptedNotifications)
                .rejected(rejectedNotifications)
                .comment(commentNotifications)
                .build();
    }

    public void markAsRead(UUID notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId);
    }

    public void createNotification(UUID userId, NotificationType type, String note, String data) {
        User user = new User();
        user.setId(userId);

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .note(note)
                .data(data)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    public Notification createNotification(UUID userId, NotificationDto notificationDto) {
        User user = new User();
        user.setId(userId);

        Notification notification = Notification.builder()
                .user(user)
                .type(notificationDto.getType())
                .note(notificationDto.getNote())
                .data(notificationDto.getData())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }
}