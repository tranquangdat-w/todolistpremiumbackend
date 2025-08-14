package com.fsoft.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsoft.dto.AcceptedNotificationDto;
import com.fsoft.dto.InvitationDto;
import com.fsoft.dto.RejectedNotificationDto;
import com.fsoft.model.Boards;
import com.fsoft.model.Invitation;
import com.fsoft.model.NotificationType;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
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
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

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

        // Send notification when updating invitation status
        try {
            Boards board = invitation.getBoard();
            User invitedUser = invitation.getInvitedUser();
            User inviterUser = invitation.getInviterUser();

            if (board == null || invitedUser == null || inviterUser == null) {
                throw new EntityNotFoundException("Missing required entities for invitation");
            }

            if ("ACCEPTED".equals(newStatus)) {
                // Add user to the board when they accept the invitation
                // TODO: Need to create a BoardMembers relation table to store many-to-many relationship between Board and User

                // Create ACCEPTED notification for the inviter
                AcceptedNotificationDto acceptedDto = AcceptedNotificationDto.builder()
                        .boardId(board.getId())
                        .boardTitle(board.getTitle())
                        .invitedUsername(invitedUser.getUsername())
                        .invitedAvatar(invitedUser.getAvatar())
                        .build();

                String data = objectMapper.writeValueAsString(acceptedDto);
                String note = invitedUser.getUsername() + " has accepted the invitation to join board " + board.getTitle();

                notificationService.createNotification(
                        inviterUser.getId(),
                        NotificationType.ACCEPTED,
                        note,
                        data
                );

                // Create notification for the invited user that they have joined the board
                String invitedUserNote = "You have joined the board " + board.getTitle();
                notificationService.createNotification(
                        invitedUser.getId(),
                        NotificationType.ACCEPTED,
                        invitedUserNote,
                        data
                );

            } else if ("DECLINED".equals(newStatus) || "REJECTED".equals(newStatus)) {
                // Create REJECTED notification for the inviter
                RejectedNotificationDto rejectedDto = RejectedNotificationDto.builder()
                        .boardId(board.getId())
                        .boardTitle(board.getTitle())
                        .invitedUsername(invitedUser.getUsername())
                        .invitedAvatar(invitedUser.getAvatar())
                        .build();

                String data = objectMapper.writeValueAsString(rejectedDto);
                String note = invitedUser.getUsername() + " has declined the invitation to join board " + board.getTitle();

                notificationService.createNotification(
                        inviterUser.getId(),
                        NotificationType.REJECTED,
                        note,
                        data
                );

                // Create notification for the invited user confirming they declined
                String invitedUserNote = "You have declined the invitation to join board " + board.getTitle();
                notificationService.createNotification(
                        invitedUser.getId(),
                        NotificationType.REJECTED,
                        invitedUserNote,
                        data
                );
            }
        } catch (JsonProcessingException e) {
            // Handle JSON conversion error
            e.printStackTrace();
            throw new RuntimeException("Error processing invitation notification: " + e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            // Handle entity not found error
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
            throw new RuntimeException("Error processing invitation: " + e.getMessage(), e);
        }
    }

    @Override
    public void createTestInvitations(String userId) {
        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for userId: " + userId);
        }

        // Find user with provided ID
        User invitedUser = userRepository.findById(userUUID)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        // Find some other users to act as inviters
        List<User> potentialInviters = userRepository.findAll();

        // Find some boards to link with invitations
        List<Boards> boards = boardRepository.findAll();

        // If there are no other users or boards, cannot create test invitations
        if (potentialInviters.isEmpty() || boards.isEmpty()) {
            throw new EntityNotFoundException("Cannot create invitations due to missing users or boards");
        }

        // Create 3 sample invitations with different statuses
        // Invitation 1: PENDING
        createInvitation(
            invitedUser,
            getFirstUserNotEqual(potentialInviters, invitedUser),
            boards.get(0),
            "PENDING"
        );

        // Invitation 2: ACCEPTED
        if (boards.size() > 1) {
            createInvitation(
                invitedUser,
                getFirstUserNotEqual(potentialInviters, invitedUser),
                boards.get(1),
                "ACCEPTED"
            );
        }

        // Invitation 3: DECLINED
        if (boards.size() > 2) {
            createInvitation(
                invitedUser,
                getFirstUserNotEqual(potentialInviters, invitedUser),
                boards.get(2),
                "DECLINED"
            );
        }
    }

    private User getFirstUserNotEqual(List<User> users, User excludeUser) {
        if (users == null || users.isEmpty()) {
            throw new EntityNotFoundException("No users found in the system");
        }

        return users.stream()
            .filter(user -> !user.getId().equals(excludeUser.getId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Could not find another user to send invitation"));
    }

    private void createInvitation(User invitedUser, User inviterUser, Boards board, String status) {
        Invitation invitation = Invitation.builder()
            .board(board)
            .invitedUser(invitedUser)
            .inviterUser(inviterUser)
            .status(status)
            .sentAt(LocalDateTime.now())
            .respondedAt("PENDING".equals(status) ? null : LocalDateTime.now())
            .build();

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
