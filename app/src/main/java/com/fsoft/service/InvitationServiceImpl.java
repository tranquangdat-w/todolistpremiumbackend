package com.fsoft.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fsoft.dto.InvitationDto;
import com.fsoft.model.Invitation;
import com.fsoft.model.User;
import com.fsoft.repository.InvitationRepository;
import com.fsoft.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;

    @Override
    public List<InvitationDto> getUserInvitations(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return invitationRepository.findAllByInvitedUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateInvitationStatus(UUID invitationId, String newStatus) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        invitation.setStatus(newStatus);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }

    private InvitationDto mapToDto(Invitation invitation) {
        return InvitationDto.builder()
                .id(invitation.getId())
                .boardTitle(invitation.getBoard().getTitle())
                .inviterUsername(invitation.getInviterUser().getUsername())
                .inviterAvatar(invitation.getInviterUser().getAvatar())
                .invitedUsername(invitation.getInvitedUser().getUsername())
                .invitedAvatar(invitation.getInvitedUser().getAvatar())
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .respondedAt(invitation.getRespondedAt())
                .build();
    }
}
