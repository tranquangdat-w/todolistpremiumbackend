package com.fsoft.controller;

import com.fsoft.dto.InvitationDto;
import com.fsoft.model.Board;
import com.fsoft.model.BoardMember;
import com.fsoft.model.User;
import com.fsoft.repository.BoardMemberRepository;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.UserRepository;
import com.fsoft.service.InvitationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NotificationController {
  private final InvitationServiceImpl invitationService;

  private final SimpMessagingTemplate messagingTemplate;

  private final UserRepository userRepository;

  private final BoardRepository boardRepository;

  private final BoardMemberRepository boardMemberRepository;

  @MessageMapping("/send-invitation")
  public void sendMessage(InvitationDto message) {
    User inviter = userRepository.findByEmail(message.getInviterUsername())
        .orElseThrow(() -> new EntityNotFoundException("Inviter user not found"));
    User invited = userRepository.findByEmail(message.getInvitedUsername())
        .orElseThrow(() -> new EntityNotFoundException("Invited user not found"));
    Board board = boardRepository.findById(message.getBoardId())

        .orElseThrow(() -> new EntityNotFoundException("Board not found"));
    UUID invitationId = invitationService.createInvitation(inviter, invited, board);
    // message.setInviterUsername(inviter.getEmail());
    message.setInvitationId(invitationId);
    // message.setInviterUsername(inviter.getUsername());
    messagingTemplate.convertAndSendToUser(
        message.getInvitedUsername(),
        "/queue/messages",
        Map.of(
            "type", "createInvitation",
            "payload", message));
  }

    @MessageMapping("/update-invitation")
    public void updateInvitation(InvitationDto message) {
        UUID invitationId = message.getInvitationId();
        String status = message.getStatus();
        if (status.equals("accept")) {
            User invited = userRepository.findByEmail(message.getInvitedUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Invited user not found"));
            boardMemberRepository.save(BoardMember.builder()
                    .boardId(message.getBoardId())
                    .userId(invited.getId())
                    .joinedAt(LocalDateTime.now())
                    .build());
        }
        invitationService.updateInvitationStatus(invitationId, status);
        String inviterUsername = message.getInviterUsername();
        message.setInviterUsername(message.getInvitedUsername());
        message.setInvitedUsername(inviterUsername);
        messagingTemplate.convertAndSendToUser(
                message.getInvitedUsername(),
                "/queue/messages",
                Map.of(
                        "type", "updateInvitation",
                        "payload", message
                )
        );
    }

//    @MessageMapping("/send-comment")
//    public void sendComment(CommentNotificationDto dto, Principal principal) { ... }
//
//    @MessageMapping("/send-assignment")
//    public void sendAssignment(CardAssignmenNotificationtDto dto, Principal principal) { ... }

    // Listen for subscription events
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if (principal == null) return;

        String destination = accessor.getDestination();
        String userEmail = principal.getName(); // principal name should be email (or userId depending on your handshake handler)

        if (destination != null && destination.equals("/user/queue/messages")) {
            List<InvitationDto> invitations = invitationService.getUserInvitations(userEmail);
            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/messages",
                    Map.of(
                            "type", "invitations",
                            "payload", invitations
                    )
            );
        }
    }
}
