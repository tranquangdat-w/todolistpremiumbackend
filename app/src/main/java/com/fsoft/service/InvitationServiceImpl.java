package com.fsoft.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fsoft.dto.InvitationDto;
import com.fsoft.model.Boards;
import com.fsoft.model.Invitation;
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

    @Override
    public void createTestInvitations(String userId) {
        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for userId: " + userId);
        }

        // Tìm người dùng với ID cung cấp
        User invitedUser = userRepository.findById(userUUID)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        // Tìm một số người dùng khác để đóng vai trò là người mời
        List<User> potentialInviters = userRepository.findAll();

        // Tìm một số bảng để liên kết với lời mời
        List<Boards> boards = boardRepository.findAll();

        // Nếu không có người dùng khác hoặc bảng, tạo một lời mời mẫu
        if (potentialInviters.isEmpty() || boards.isEmpty()) {
            throw new EntityNotFoundException("Không thể tạo lời mời vì thiếu người dùng hoặc bảng");
        }

        // Tạo 3 lời mời mẫu với trạng thái khác nhau
        // Lời mời 1: PENDING
        createInvitation(
            invitedUser,
            getFirstUserNotEqual(potentialInviters, invitedUser),
            boards.get(0),
            "PENDING"
        );

        // Lời mời 2: ACCEPTED
        if (boards.size() > 1) {
            createInvitation(
                invitedUser,
                getFirstUserNotEqual(potentialInviters, invitedUser),
                boards.get(1),
                "ACCEPTED"
            );
        }

        // Lời mời 3: DECLINED
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
        return users.stream()
            .filter(user -> !user.equals(excludeUser))
            .findFirst()
            .orElse(users.get(0)); // Fallback to any user if no other users found
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
