package com.fsoft.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(invitationService.getUserInvitations(user.getId()));
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<Void> updateInvitationStatus(
            @PathVariable UUID invitationId,
            @RequestParam String status) {
        invitationService.updateInvitationStatus(invitationId, status);
        return ResponseEntity.ok().build();
    }
}
