package com.fsoft.service;

import java.util.List;
import java.util.UUID;

import com.fsoft.dto.InvitationDto;

public interface InvitationService {
    List<InvitationDto> getUserInvitations(UUID userId);
    void updateInvitationStatus(UUID invitationId, String newStatus);
    void createTestInvitations(String userId);
}
