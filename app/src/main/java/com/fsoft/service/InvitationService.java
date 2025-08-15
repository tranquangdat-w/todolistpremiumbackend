package com.fsoft.service;

import com.fsoft.dto.CreateInvitationDto;
import com.fsoft.dto.InvitationDto;
import com.fsoft.model.Board;
import com.fsoft.model.Invitation;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.InvitationRepository;
import com.fsoft.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public InvitationDto createInvitation(CreateInvitationDto createDto, UUID inviterUserId) {
        log.info("Creating invitation for board {} by user {} to {}",
                createDto.getBoardId(), inviterUserId, createDto.getEmailOrUsername());

        // Fix PostgreSQL parameter binding: Find user by email or username with proper parameter binding
        User invitedUser = userRepository.findByEmailOrUsername(createDto.getEmailOrUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email or username: " + createDto.getEmailOrUsername()));

        // Get inviter information
        User inviterUser = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new RuntimeException("Inviter user not found"));

        // Get board information
        Board board = boardRepository.findById(createDto.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Check cannot invite yourself
        if (invitedUser.getId().equals(inviterUserId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        // Check if pending invitation already exists - Fixed parameter binding
        if (invitationRepository.existsPendingInvitation(createDto.getBoardId(), invitedUser.getId(), Invitation.InvitationStatus.PENDING)) {
            throw new RuntimeException("Invitation already exists for this user");
        }

        // Create new invitation with proper enum handling
        Invitation invitation = new Invitation();
        invitation.setBoardId(createDto.getBoardId());
        invitation.setInvitedUserId(invitedUser.getId());
        invitation.setInviterUserId(inviterUserId);
        invitation.setStatus(Invitation.InvitationStatus.PENDING); // Use enum instead of string
        invitation.setSentAt(LocalDateTime.now());
        // Removed setMessage as it doesn't exist in Invitation entity

        Invitation saved = invitationRepository.save(invitation);

        // Create notification for invited user
        createInvitationNotification(saved, invitedUser, inviterUser, board);

        log.info("Invitation created successfully with ID: {}", saved.getId());
        return convertToDto(saved, invitedUser, inviterUser, board);
    }

    public List<InvitationDto> getReceivedInvitations(UUID userId) {
        List<Invitation> invitations = invitationRepository.findByInvitedUserIdOrderBySentAtDesc(userId);
        return invitations.stream()
                .map(invitation -> {
                    User inviterUser = userRepository.findById(invitation.getInviterUserId()).orElse(null);
                    User invitedUser = userRepository.findById(invitation.getInvitedUserId()).orElse(null);
                    Board board = boardRepository.findById(invitation.getBoardId()).orElse(null);
                    return convertToDto(invitation, invitedUser, inviterUser, board);
                })
                .collect(Collectors.toList());
    }

    public List<InvitationDto> getSentInvitations(UUID userId) {
        List<Invitation> invitations = invitationRepository.findByInviterUserIdOrderBySentAtDesc(userId);
        return invitations.stream()
                .map(invitation -> {
                    User inviterUser = userRepository.findById(invitation.getInviterUserId()).orElse(null);
                    User invitedUser = userRepository.findById(invitation.getInvitedUserId()).orElse(null);
                    Board board = boardRepository.findById(invitation.getBoardId()).orElse(null);
                    return convertToDto(invitation, invitedUser, inviterUser, board);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public InvitationDto respondToInvitation(UUID invitationId, Invitation.InvitationStatus response, UUID userId) {
        log.info("User {} responding to invitation {} with {}", userId, invitationId, response);

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        // Kiểm tra quyền respond
        if (!invitation.getInvitedUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to respond to this invitation");
        }

        // Kiểm tra status hiện tại
        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation has already been responded to");
        }

        // Lấy thông tin các user và board để hiển thị
        User invitedUser = userRepository.findById(invitation.getInvitedUserId()).orElse(null);
        User inviterUser = userRepository.findById(invitation.getInviterUserId()).orElse(null);
        Board board = boardRepository.findById(invitation.getBoardId()).orElse(null);

        // Cập nhật invitation
        invitation.setStatus(response);
        invitation.setRespondedAt(LocalDateTime.now());

        Invitation updated = invitationRepository.save(invitation);

        // Nếu accept thì thêm user vào board_members
        if (response == Invitation.InvitationStatus.ACCEPT) {
            addUserToBoardMembers(invitation.getBoardId(), invitation.getInvitedUserId());
        }

        // Tạo notification cho inviter
        createResponseNotification(updated, invitedUser, inviterUser, board);

        log.info("Invitation {} responded successfully", invitationId);
        return convertToDto(updated, invitedUser, inviterUser, board);
    }

    private void createInvitationNotification(Invitation invitation, User invitedUser, User inviterUser, Board board) {
        try {
            String insertNotificationSql = """
                INSERT INTO notifications (user_id, type, note, data, isread, createdat)
                VALUES (?, ?, ?, ?::jsonb, false, NOW())
                """;

            String note = String.format("You have received a board invitation from %s for board '%s'",
                    inviterUser.getName(), board.getTitle());

            String jsonData = String.format("""
                {"invitation_id": "%s", "board_id": "%s", "board_title": "%s", "inviter_user_id": "%s", "inviter_name": "%s"}
                """, invitation.getId(), invitation.getBoardId(), board.getTitle(),
                invitation.getInviterUserId(), inviterUser.getName());

            jdbcTemplate.update(insertNotificationSql, invitation.getInvitedUserId(),
                    "INVITATION_RECEIVED", note, jsonData);
            log.info("Created invitation notification for user: {}", invitedUser.getId());

        } catch (Exception e) {
            log.error("Failed to create invitation notification: {}", e.getMessage());
        }
    }

    private void createResponseNotification(Invitation invitation, User invitedUser, User inviterUser, Board board) {
        try {
            String insertNotificationSql = """
                INSERT INTO notifications (user_id, type, note, data, isread, createdat)
                VALUES (?, ?, ?, ?::jsonb, false, NOW())
                """;

            String notificationType = invitation.getStatus() == Invitation.InvitationStatus.ACCEPT
                ? "INVITATION_ACCEPTED" : "INVITATION_REJECTED";

            String note = String.format("%s has %s your board invitation for '%s'",
                    invitedUser != null ? invitedUser.getName() : "Unknown User",
                    invitation.getStatus() == Invitation.InvitationStatus.ACCEPT ? "accepted" : "rejected",
                    board != null ? board.getTitle() : "Unknown Board");

            String jsonData = String.format("""
                {"invitation_id": "%s", "board_id": "%s", "board_title": "%s", "invited_user_id": "%s", "invited_user_name": "%s", "status": "%s"}
                """, invitation.getId(), invitation.getBoardId(),
                board != null ? board.getTitle() : "Unknown Board",
                invitation.getInvitedUserId(),
                invitedUser != null ? invitedUser.getName() : "Unknown User",
                invitation.getStatus().toString().toLowerCase());

            jdbcTemplate.update(insertNotificationSql, invitation.getInviterUserId(), notificationType, note, jsonData);
            log.info("Created response notification for invitation: {}", invitation.getId());

        } catch (Exception e) {
            log.error("Failed to create response notification: {}", e.getMessage());
        }
    }

    private void addUserToBoardMembers(UUID boardId, UUID userId) {
        try {
            // Fix lỗi PostgreSQL: Sử dụng proper parameter binding
            String insertMemberSql = """
                INSERT INTO board_members (board_id, user_id, joined_at)
                VALUES (?, ?, NOW())
                ON CONFLICT (board_id, user_id) DO NOTHING
                """;

            int rowsAffected = jdbcTemplate.update(insertMemberSql, boardId, userId);
            if (rowsAffected > 0) {
                log.info("Added user {} to board {} successfully", userId, boardId);
            } else {
                log.info("User {} is already a member of board {}", userId, boardId);
            }

        } catch (Exception e) {
            log.error("Failed to add user {} to board {}: {}", userId, boardId, e.getMessage());
            throw new RuntimeException("Failed to add user to board", e);
        }
    }

    private InvitationDto convertToDto(Invitation invitation, User invitedUser, User inviterUser, Board board) {
        return InvitationDto.builder()
                .id(invitation.getId())
                .boardId(invitation.getBoardId())
                .boardTitle(board != null ? board.getTitle() : "Unknown Board")
                .invitedUserId(invitation.getInvitedUserId())
                .invitedUserName(invitedUser != null ? invitedUser.getName() : "Unknown User")
                .invitedUserEmail(invitedUser != null ? invitedUser.getEmail() : "Unknown Email")
                .inviterUserId(invitation.getInviterUserId())
                .inviterUserName(inviterUser != null ? inviterUser.getName() : "Unknown User")
                .inviterUserEmail(inviterUser != null ? inviterUser.getEmail() : "Unknown Email")
                .status(invitation.getStatus())
                .sentAt(invitation.getSentAt())
                .respondedAt(invitation.getRespondedAt())
                // Removed .message() call as Invitation entity doesn't have message field
                .build();
    }
}

