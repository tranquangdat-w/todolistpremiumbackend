package com.fsoft.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsoft.dto.InvitationDto;
import com.fsoft.model.User;
import com.fsoft.service.InvitationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public ResponseEntity<List<InvitationDto>> getUserInvitations(@AuthenticationPrincipal User user) {
        try {
            // Handle case when user is null - user not authenticated
            if (user == null) {
                return ResponseEntity.status(HttpStatus.OK).body(List.of()); // Return empty list with status 200 instead of 403
            }

            UUID userId = user.getId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.OK).body(List.of());
            }

            return ResponseEntity.ok(invitationService.getUserInvitations(userId));
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            // Return empty list in case of any exception
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<Void> updateInvitationStatus(
            @PathVariable UUID invitationId,
            @RequestParam String status) {
        invitationService.updateInvitationStatus(invitationId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test-create")
    public ResponseEntity<?> createTestInvitations() {
        try {
            // User ID from request
            String userId = "99d5bb72-1ada-47b1-93e5-5a83bfa0a041";

            // Create test invitations
            invitationService.createTestInvitations(userId);

            return ResponseEntity.ok("Test invitations created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating test invitations: " + e.getMessage());
        }
    }
}
