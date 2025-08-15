package com.fsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Service
public class DatabaseTriggerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Gọi stored procedure để lấy thống kê board
     */
    public Map<String, Object> getBoardStatistics(UUID boardId) {
        String sql = "SELECT * FROM get_board_statistics(?)";

        return jdbcTemplate.queryForMap(sql, boardId);
    }

    /**
     * Gọi stored procedure để cleanup expired invitations
     */
    public int cleanupExpiredInvitations() {
        String sql = "SELECT cleanup_expired_invitations()";

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }

    /**
     * Trigger notification thủ công (nếu cần)
     */
    public void triggerManualNotification(UUID userId, String type, String note, String jsonData) {
        String sql = """
            INSERT INTO notifications (user_id, type, note, data, isread, createdat)
            VALUES (?, ?, ?, ?::jsonb, false, NOW())
            """;

        jdbcTemplate.update(sql, userId, type, note, jsonData);
    }

    /**
     * Gọi custom function để update card position với transaction
     */
    public void updateCardPositions(UUID columnId, String positionUpdates) {
        String sql = """
            DO $$
            DECLARE
                update_data jsonb := ?::jsonb;
                card_update jsonb;
            BEGIN
                FOR card_update IN SELECT * FROM jsonb_array_elements(update_data)
                LOOP
                    UPDATE cards 
                    SET position = (card_update->>'position')::numeric
                    WHERE id = (card_update->>'id')::uuid
                    AND column_id = ?;
                END LOOP;
            END $$;
            """;

        jdbcTemplate.update(sql, positionUpdates, columnId);
    }

    /**
     * Execute custom SQL với transaction
     */
    public void executeCustomProcedure(String procedureName, Object... params) {
        StringBuilder sql = new StringBuilder("CALL ").append(procedureName).append("(");

        for (int i = 0; i < params.length; i++) {
            sql.append("?");
            if (i < params.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        jdbcTemplate.update(sql.toString(), params);
    }
}
