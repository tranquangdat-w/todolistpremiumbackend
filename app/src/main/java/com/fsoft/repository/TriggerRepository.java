package com.fsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fsoft.model.Notification;
import java.util.UUID;
import java.util.List;

@Repository
public interface TriggerRepository extends JpaRepository<Notification, UUID> {

    /**
     * Gọi stored procedure để lấy thống kê board
     */
    @Query(value = "SELECT * FROM get_board_statistics(:boardId)", nativeQuery = true)
    List<Object[]> getBoardStatistics(@Param("boardId") UUID boardId);

    /**
     * Gọi function cleanup expired invitations
     */
    @Query(value = "SELECT cleanup_expired_invitations()", nativeQuery = true)
    Integer cleanupExpiredInvitations();

    /**
     * Trigger notification thủ công
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO notifications (user_id, type, note, data, isread, createdat)
        VALUES (:userId, :type, :note, :jsonData::jsonb, false, NOW())
        """, nativeQuery = true)
    void createManualNotification(@Param("userId") UUID userId,
                                 @Param("type") String type,
                                 @Param("note") String note,
                                 @Param("jsonData") String jsonData);

    /**
     * Bulk update card positions using database function
     */
    @Modifying
    @Transactional
    @Query(value = """
        DO $$
        DECLARE
            update_data jsonb := :positionUpdates::jsonb;
            card_update jsonb;
        BEGIN
            FOR card_update IN SELECT * FROM jsonb_array_elements(update_data)
            LOOP
                UPDATE cards 
                SET position = (card_update->>'position')::numeric
                WHERE id = (card_update->>'id')::uuid
                AND column_id = :columnId;
            END LOOP;
        END $$;
        """, nativeQuery = true)
    void updateCardPositions(@Param("columnId") UUID columnId,
                           @Param("positionUpdates") String positionUpdates);
}
