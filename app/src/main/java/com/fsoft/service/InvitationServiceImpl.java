package com.fsoft.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fsoft.model.Board;
import com.fsoft.repository.BoardRepository;
import jakarta.transaction.Transactional;
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

    private final BoardRepository boardRepository;
    public List<InvitationDto> getUserInvitations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return invitationRepository.findAllByInvitedUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<InvitationDto> getUserInvitations(UUID userId) {
//        User user = userRepository.findByEmail(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        return invitationRepository.findAllByInvitedUser(user)
//                .stream()
//                .map(this::mapToDto)
//                .collect(Collectors.toList());
//    }

    @Transactional
    public UUID createInvitation(User inviter, User invited, Board board) {
        Invitation entity = Invitation.builder()
                .board(board)
                .inviterUser(inviter)
                .invitedUser(invited)
                .status("pending")
                .sentAt(LocalDateTime.now())
                .build();

        Invitation saved = invitationRepository.save(entity);
        return saved.getId();
    }

    @Transactional
    public void updateInvitationStatus(UUID invitationId, String newStatus) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        invitation.setStatus(newStatus);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }

    private InvitationDto mapToDto(Invitation invitation) {
        return InvitationDto.builder()
                .invitationId(invitation.getId())
                .boardId(invitation.getBoard().getId())
                .boardTitle(invitation.getBoard().getTitle())
                .inviterUsername(invitation.getInviterUser().getEmail())
                .inviterAvatar(invitation.getInviterUser().getAvatar())
                .invitedUsername(invitation.getInvitedUser().getEmail())
                .invitedAvatar(invitation.getInvitedUser().getAvatar())
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .respondedAt(invitation.getRespondedAt())
                .build();
    }
}
