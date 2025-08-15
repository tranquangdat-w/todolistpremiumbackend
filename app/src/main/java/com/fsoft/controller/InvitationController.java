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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fsoft.dto.CreateInvitationDto;
import com.fsoft.dto.InvitationDto;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.InvitationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping
    public ResponseEntity<List<InvitationDto>> getUserInvitations(@AuthenticationPrincipal JwtPayload jwtPayload) {
        try {
            // Handle case when user is null - user not authenticated
            if (jwtPayload == null) {
                return ResponseEntity.status(HttpStatus.OK).body(List.of());
            }

            UUID userId = jwtPayload.getId();
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

    @PostMapping
    public ResponseEntity<InvitationDto> createInvitation(
            @RequestBody CreateInvitationDto createInvitationDto,
            @AuthenticationPrincipal JwtPayload jwtPayload) {
        try {
            if (jwtPayload == null || jwtPayload.getId() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            InvitationDto createdInvitation = invitationService.createInvitation(
                    createInvitationDto.getUsername(),
                    createInvitationDto.getBoardId(),
                    jwtPayload.getId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvitation);
        } catch (IllegalStateException e) {
            // Handle case when the invitation already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
