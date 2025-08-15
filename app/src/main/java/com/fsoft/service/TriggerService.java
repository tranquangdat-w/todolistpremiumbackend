package com.fsoft.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriggerService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void installTriggers() {
        try {
            log.info("Installing database triggers...");

            // Đọc file triggers.sql
            ClassPathResource resource = new ClassPathResource("triggers.sql");
            String triggerSql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            // Thực thi SQL để cài đặt triggers
            jdbcTemplate.execute(triggerSql);

            log.info("Database triggers installed successfully");
        } catch (IOException e) {
            log.error("Failed to read triggers.sql file", e);
            throw new RuntimeException("Failed to read triggers.sql file", e);
        } catch (Exception e) {
            log.error("Failed to install database triggers", e);
            throw new RuntimeException("Failed to install database triggers", e);
        }
    }

    @Transactional
    public void uninstallTriggers() {
        try {
            log.info("Uninstalling database triggers...");

            // Xóa các triggers
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS invitation_notification_trigger ON invitations");
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS invitation_status_change_trigger ON invitations");
            jdbcTemplate.execute("DROP TRIGGER IF EXISTS card_completion_trigger ON cards");

            // Xóa các functions
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS create_invitation_notification()");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS update_card_stats()");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS update_modified_column()");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS get_board_statistics(UUID)");
            jdbcTemplate.execute("DROP FUNCTION IF EXISTS cleanup_expired_invitations()");

            log.info("Database triggers uninstalled successfully");
        } catch (Exception e) {
            log.error("Failed to uninstall database triggers", e);
            throw new RuntimeException("Failed to uninstall database triggers", e);
        }
    }

    @Transactional
    public void manualTriggerInvitationAccept(UUID invitationId) {
        try {
            log.info("Manually triggering invitation acceptance for ID: {}", invitationId);

            // Lấy thông tin invitation
            String selectSql = """
                SELECT invited_user_id, inviter_user_id, board_id, status 
                FROM invitations 
                WHERE id = ?
                """;

            Map<String, Object> invitation = jdbcTemplate.queryForMap(selectSql, invitationId);

            if ("accept".equals(invitation.get("status"))) {
                UUID inviterUserId = (UUID) invitation.get("inviter_user_id");
                UUID invitedUserId = (UUID) invitation.get("invited_user_id");
                UUID boardId = (UUID) invitation.get("board_id");

                // Tạo notification cho inviter
                String insertNotificationSql = """
                    INSERT INTO notifications (user_id, type, note, data, isread, createdat)
                    VALUES (?, 'INVITATION_ACCEPTED', 'Your board invitation has been accepted', 
                    ?::jsonb, false, NOW())
                    """;

                String jsonData = String.format("""
                    {"invitation_id": "%s", "board_id": "%s", "invited_user_id": "%s", "status": "accept"}
                    """, invitationId, boardId, invitedUserId);

                jdbcTemplate.update(insertNotificationSql, inviterUserId, jsonData);

                // Thêm user vào board_members
                String insertMemberSql = """
                    INSERT INTO board_members (board_id, user_id, joined_at)
                    VALUES (?, ?, NOW())
                    ON CONFLICT (board_id, user_id) DO NOTHING
                    """;

                jdbcTemplate.update(insertMemberSql, boardId, invitedUserId);

                log.info("Manual trigger executed successfully for invitation: {}", invitationId);
            } else {
                throw new IllegalStateException("Invitation is not in accepted status");
            }

        } catch (Exception e) {
            log.error("Failed to execute manual trigger for invitation: {}", invitationId, e);
            throw new RuntimeException("Failed to execute manual trigger", e);
        }
    }

    public int cleanupExpiredInvitations() {
        try {
            log.info("Cleaning up expired invitations...");

            String callProcedure = "SELECT cleanup_expired_invitations()";
            Integer deletedCount = jdbcTemplate.queryForObject(callProcedure, Integer.class);

            log.info("Cleaned up {} expired invitations", deletedCount);
            return deletedCount != null ? deletedCount : 0;

        } catch (Exception e) {
            log.error("Failed to cleanup expired invitations", e);
            throw new RuntimeException("Failed to cleanup expired invitations", e);
        }
    }

    public Map<String, Object> getBoardStatistics(UUID boardId) {
        try {
            log.info("Getting board statistics for board: {}", boardId);

            String callProcedure = "SELECT * FROM get_board_statistics(?)";

            Map<String, Object> result = jdbcTemplate.queryForMap(callProcedure, boardId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCards", result.get("total_cards"));
            stats.put("completedCards", result.get("completed_cards"));
            stats.put("pendingCards", result.get("pending_cards"));
            stats.put("totalMembers", result.get("total_members"));
            stats.put("completionRate", result.get("completion_rate"));

            log.info("Retrieved board statistics: {}", stats);
            return stats;

        } catch (Exception e) {
            log.error("Failed to get board statistics for board: {}", boardId, e);
            throw new RuntimeException("Failed to get board statistics", e);
        }
    }

    @Transactional
    public void triggerManualNotification(UUID userId, String type, String note, String jsonData) {
        try {
            log.info("Creating manual notification for user: {} with type: {}", userId, type);

            String insertSql = """
                INSERT INTO notifications (user_id, type, note, data, isread, createdat)
                VALUES (?, ?, ?, ?::jsonb, false, NOW())
                """;

            jdbcTemplate.update(insertSql, userId, type, note, jsonData);

            log.info("Manual notification created successfully");
        } catch (Exception e) {
            log.error("Failed to create manual notification", e);
            throw new RuntimeException("Failed to create manual notification", e);
        }
    }

    @Transactional
    public void updateCardPositions(UUID columnId, String positionUpdates) {
        try {
            log.info("Updating card positions for column: {}", columnId);

            // Logic để update positions - có thể parse JSON và update từng card
            // Đây là ví dụ đơn giản, bạn có thể mở rộng theo nhu cầu
            String updateSql = """
                UPDATE cards 
                SET position = position + 1 
                WHERE column_id = ?
                """;

            int updatedRows = jdbcTemplate.update(updateSql, columnId);

            log.info("Updated positions for {} cards", updatedRows);
        } catch (Exception e) {
            log.error("Failed to update card positions", e);
            throw new RuntimeException("Failed to update card positions", e);
        }
    }
}
