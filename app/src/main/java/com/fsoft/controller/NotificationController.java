package com.fsoft.controller;

import com.fsoft.dto.NotificationResponseDto;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fsoft.dto.NotificationDto;
import com.fsoft.model.Notification;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationResponseDto> getUserNotifications(@AuthenticationPrincipal JwtPayload jwtPayload) {
        try {
            UUID userId = jwtPayload.getId();
            NotificationResponseDto notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal JwtPayload jwtPayload) {
        try {
            UUID userId = jwtPayload.getId();
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @AuthenticationPrincipal JwtPayload jwtPayload,
            @RequestBody NotificationDto notificationDto) {
        try {
            UUID userId = jwtPayload.getId();
            Notification notification = notificationService.createNotification(userId, notificationDto);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
