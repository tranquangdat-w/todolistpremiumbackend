package com.fsoft.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .toList();
    }

    @Override
    @Transactional
    public void updateInvitationStatus(UUID invitationId, String newStatus) {
        log.info("Updating invitation status - ID: {}, New Status: {}", invitationId, newStatus);

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        String oldStatus = invitation.getStatus();
        invitation.setStatus(newStatus);
        invitation.setRespondedAt(LocalDateTime.now());

        // Save invitation - trigger will automatically run when status changes
        invitationRepository.save(invitation);

        log.info("Invitation status updated from {} to {} - Trigger will handle notification and board membership",
                oldStatus, newStatus);

        // Send notification based on status change
        try {
            if ("accept".equals(newStatus)) {
                sendAcceptedNotification(invitation);
            } else if ("reject".equals(newStatus)) {
                sendRejectedNotification(invitation);
            }
        } catch (Exception e) {
            log.error("Failed to send status change notification for status: {}", newStatus, e);
        }
    }

    @Override
    public InvitationDto createInvitation(String username, UUID boardId, UUID inviterUserId) {
        log.info("Creating invitation - Username: {}, Board: {}, Inviter: {}",
                username, boardId, inviterUserId);

        User invitedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Invited user not found with username: " + username));

        User inviterUser = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Inviter user not found"));

        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        // Check if user is trying to invite themselves
        if (invitedUser.getId().equals(inviterUserId)) {
            throw new IllegalArgumentException("Cannot invite yourself to a board");
        }

        // Check if invitation already exists for this user and board with pending or accepted status
        List<Invitation> existingInvitations = invitationRepository.findAllByInvitedUserAndBoardAndStatus(invitedUser, board, "pending");
        boolean hasPendingInvitation = !existingInvitations.isEmpty();

        if (hasPendingInvitation) {
            throw new IllegalArgumentException("User already has a pending invitation for this board");
        }

        // Also check for accepted invitations
        List<Invitation> acceptedInvitations = invitationRepository.findAllByInvitedUserAndBoardAndStatus(invitedUser, board, "accept");
        boolean hasAcceptedInvitation = !acceptedInvitations.isEmpty();

        if (hasAcceptedInvitation) {
            throw new IllegalArgumentException("User is already a member of this board");
        }

        Invitation invitation = new Invitation();
        invitation.setBoard(board);
        invitation.setInvitedUser(invitedUser);
        invitation.setInviterUser(inviterUser);
        invitation.setStatus("pending");
        invitation.setSentAt(LocalDateTime.now());

        // Save invitation first
        Invitation savedInvitation = invitationRepository.save(invitation);

        // Send invitation notification to the invited user
        try {
            sendInvitationNotification(savedInvitation);
        } catch (Exception e) {
            log.error("Failed to send invitation notification for invitation: {}", savedInvitation.getId(), e);
        }

        log.info("Invitation created - ID: {} with notification sent", savedInvitation.getId());

        return mapToDto(savedInvitation);
    }

    @Override
    public void createTestInvitations(String userId) {
        log.info("Creating test invitations for user: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);
            User user = userRepository.findById(userUuid)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Find some boards to create test invitations (exclude boards owned by this user)
            List<Boards> boards = boardRepository.findAll().stream()
                    .filter(board -> !board.getUser().getId().equals(userUuid))
                    .limit(3)
                    .toList();

            for (Boards board : boards) {
                Invitation invitation = new Invitation();
                invitation.setBoard(board);
                invitation.setInvitedUser(user);
                invitation.setInviterUser(board.getUser()); // Board owner is the inviter
                invitation.setStatus("pending");
                invitation.setSentAt(LocalDateTime.now());

                // Save invitation
                Invitation savedInvitation = invitationRepository.save(invitation);

                // Send invitation notification
                try {
                    sendInvitationNotification(savedInvitation);
                } catch (Exception e) {
                    log.error("Failed to send test invitation notification for invitation: {}", savedInvitation.getId(), e);
                }
            }

            log.info("Created {} test invitations for user: {}", boards.size(), userId);

        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format for userId: {}", userId, e);
            throw new IllegalArgumentException("Invalid user ID format");
        } catch (Exception e) {
            log.error("Failed to create test invitations for user: {}", userId, e);
            throw new RuntimeException("Failed to create test invitations", e);
        }
    }

    /**
     * Send invitation notification to the invited user
     * @param invitation The invitation that was created
     */
    private void sendInvitationNotification(Invitation invitation) {
        try {
            // Create notification data for invitation
            String notificationData = String.format(
                "{\"invitationId\":\"%s\",\"boardId\":\"%s\",\"boardTitle\":\"%s\",\"inviterUsername\":\"%s\",\"inviterAvatar\":\"%s\",\"sentAt\":\"%s\"}",
                invitation.getId(),
                invitation.getBoard().getId(),
                invitation.getBoard().getTitle(),
                invitation.getInviterUser().getUsername(),
                invitation.getInviterUser().getAvatar() != null ? invitation.getInviterUser().getAvatar() : "",
                invitation.getSentAt()
            );

            notificationService.createNotification(
                invitation.getInvitedUser().getId(),
                NotificationType.INVITATION,
                "You have received a board invitation from " + invitation.getInviterUser().getUsername(),
                notificationData
            );

            log.info("Sent invitation notification for invitation: {}", invitation.getId());
        } catch (Exception e) {
            log.error("Failed to send invitation notification", e);
            throw e;
        }
    }

    /**
     * Send accepted notification to the inviter user
     * @param invitation The invitation that was accepted
     */
    private void sendAcceptedNotification(Invitation invitation) {
        try {
            // Create notification data for accepted invitation
            String notificationData = String.format(
                "{\"invitationId\":\"%s\",\"boardId\":\"%s\",\"boardTitle\":\"%s\",\"acceptedUsername\":\"%s\",\"acceptedAvatar\":\"%s\",\"acceptedAt\":\"%s\"}",
                invitation.getId(),
                invitation.getBoard().getId(),
                invitation.getBoard().getTitle(),
                invitation.getInvitedUser().getUsername(),
                invitation.getInvitedUser().getAvatar() != null ? invitation.getInvitedUser().getAvatar() : "",
                invitation.getRespondedAt()
            );

            notificationService.createNotification(
                invitation.getInviterUser().getId(),
                NotificationType.ACCEPTED,
                invitation.getInvitedUser().getUsername() + " has accepted your board invitation",
                notificationData
            );

            log.info("Sent accepted notification for invitation: {}", invitation.getId());
        } catch (Exception e) {
            log.error("Failed to send accepted notification", e);
            throw e;
        }
    }

    /**
     * Send rejected notification to the inviter user
     * @param invitation The invitation that was rejected
     */
    private void sendRejectedNotification(Invitation invitation) {
        try {
            RejectedNotificationDto rejectedDto = new RejectedNotificationDto();
            rejectedDto.setId(invitation.getId());
            rejectedDto.setBoardId(invitation.getBoard().getId());
            rejectedDto.setBoardTitle(invitation.getBoard().getTitle());
            rejectedDto.setInvitedUsername(invitation.getInvitedUser().getUsername());
            rejectedDto.setInvitedAvatar(invitation.getInvitedUser().getAvatar());
            rejectedDto.setRespondedAt(invitation.getRespondedAt());
            rejectedDto.setIsRead(false);

            String jsonData = objectMapper.writeValueAsString(rejectedDto);

            notificationService.createNotification(
                invitation.getInviterUser().getId(),
                NotificationType.REJECTED,
                invitation.getInvitedUser().getUsername() + " has rejected your board invitation",
                jsonData
            );

            log.info("Sent rejected notification for invitation: {}", invitation.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize rejected notification data", e);
            throw new RuntimeException("Failed to process rejected notification", e);
        } catch (Exception e) {
            log.error("Failed to send rejected notification", e);
            throw e;
        }
    }

    private InvitationDto mapToDto(Invitation invitation) {
        InvitationDto dto = new InvitationDto();
        dto.setId(invitation.getId());
        dto.setBoardTitle(invitation.getBoard().getTitle());
        dto.setInviterUsername(invitation.getInviterUser().getUsername());
        dto.setInviterAvatar(invitation.getInviterUser().getAvatar());
        dto.setInvitedUsername(invitation.getInvitedUser().getUsername());
        dto.setInvitedAvatar(invitation.getInvitedUser().getAvatar());
        dto.setStatus(invitation.getStatus());
        dto.setSentAt(invitation.getSentAt());
        dto.setRespondedAt(invitation.getRespondedAt());
        return dto;
    }
}

