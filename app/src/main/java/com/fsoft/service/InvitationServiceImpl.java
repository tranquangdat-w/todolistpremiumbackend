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
import com.fsoft.model.Board;
import com.fsoft.model.Invitation;
import com.fsoft.model.NotificationType;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.InvitationRepository;
import com.fsoft.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enhanced InvitationServiceImpl that fixes PostgreSQL parameter binding issues
 * and adds email/username search capability
 */
@Service("invitationServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl {
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Get all invitations for a user
     */
    public List<InvitationDto> getUserInvitations(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return invitationRepository.findAllByInvitedUser(user)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Update invitation status - fixes PostgreSQL enum parameter binding issue
     */
    @Transactional
    public void updateInvitationStatus(UUID invitationId, String newStatus) {
        log.info("Updating invitation status - ID: {}, New Status: {}", invitationId, newStatus);

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found"));

        String oldStatus = invitation.getStatus().toString();

        // Convert string to enum to fix PostgreSQL parameter binding issue
        Invitation.InvitationStatus statusEnum;
        switch (newStatus.toLowerCase()) {
            case "accept":
                statusEnum = Invitation.InvitationStatus.ACCEPT;
                break;
            case "reject":
                statusEnum = Invitation.InvitationStatus.REJECT;
                break;
            case "pending":
                statusEnum = Invitation.InvitationStatus.PENDING;
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        invitation.setStatus(statusEnum);
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

    /**
     * Create invitation with email/username search capability - fixes user discovery issue
     */
    public InvitationDto createInvitation(String emailOrUsername, UUID boardId, UUID inviterUserId) {
        log.info("Creating invitation - Email/Username: {}, Board: {}, Inviter: {}",
                emailOrUsername, boardId, inviterUserId);

        // Find user by email or username to support both input types
        User invitedUser = userRepository.findByEmailOrUsername(emailOrUsername.trim())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email/username: " + emailOrUsername));

        User inviterUser = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new EntityNotFoundException("Inviter user not found"));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        // Check if user is trying to invite themselves
        if (invitedUser.getId().equals(inviterUserId)) {
            throw new IllegalArgumentException("Cannot invite yourself to a board");
        }

        // Use the fixed existsPendingInvitation method with proper enum parameter binding
        if (invitationRepository.existsPendingInvitation(boardId, invitedUser.getId(), Invitation.InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("User already has a pending invitation for this board");
        }

        // Check if user is already accepted (member of board) using the new method
        if (invitationRepository.isUserAlreadyMember(boardId, invitedUser.getId())) {
            throw new IllegalArgumentException("User is already a member of this board");
        }

        Invitation invitation = new Invitation();
        invitation.setBoardId(board.getId());
        invitation.setInvitedUserId(invitedUser.getId());
        invitation.setInviterUserId(inviterUser.getId());
        invitation.setStatus(Invitation.InvitationStatus.PENDING);
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

    /**
     * Create test invitations for development/testing purposes
     */
    public void createTestInvitations(String userId) {
        log.info("Creating test invitations for user: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);
            User user = userRepository.findById(userUuid)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            // Find some boards to create test invitations (exclude boards owned by this user)
            List<Board> boards = boardRepository.findAll().stream()
                    .filter(board -> !board.getOwner().getId().equals(userUuid))
                    .limit(3)
                    .toList();

            for (Board board : boards) {
                Invitation invitation = new Invitation();
                invitation.setBoardId(board.getId());
                invitation.setInvitedUserId(user.getId());
                invitation.setInviterUserId(board.getOwner().getId());
                invitation.setStatus(Invitation.InvitationStatus.PENDING);
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
            rejectedDto.setRead(false);

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

    /**
     * Map Invitation entity to DTO with proper null checks and correct field mapping
     */
    private InvitationDto mapToDto(Invitation invitation) {
        InvitationDto dto = new InvitationDto();
        dto.setId(invitation.getId());
        dto.setBoardId(invitation.getBoardId());
        dto.setBoardTitle(invitation.getBoard() != null ? invitation.getBoard().getTitle() : "Unknown Board");
        dto.setInvitedUserId(invitation.getInvitedUserId());
        dto.setInvitedUserName(invitation.getInvitedUser() != null ? invitation.getInvitedUser().getUsername() : "Unknown User");
        dto.setInvitedUserEmail(invitation.getInvitedUser() != null ? invitation.getInvitedUser().getEmail() : null);
        dto.setInviterUserId(invitation.getInviterUserId());
        dto.setInviterUserName(invitation.getInviterUser() != null ? invitation.getInviterUser().getUsername() : "Unknown User");
        dto.setInviterUserEmail(invitation.getInviterUser() != null ? invitation.getInviterUser().getEmail() : null);
        dto.setStatus(invitation.getStatus());
        dto.setSentAt(invitation.getSentAt());
        dto.setRespondedAt(invitation.getRespondedAt());
        return dto;
    }
}

