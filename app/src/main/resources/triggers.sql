-- Trigger function để tự động tạo notification khi có invitation mới
CREATE OR REPLACE FUNCTION create_invitation_notification()
RETURNS TRIGGER AS $$
BEGIN
    -- Tạo notification cho user được mời khi có invitation mới
    IF TG_OP = 'INSERT' THEN
        INSERT INTO notifications (user_id, type, note, data, isread, createdat)
        VALUES (
            NEW.invited_user_id,
            'INVITATION',
            'You have received a new board invitation',
            jsonb_build_object(
                'invitation_id', NEW.id,
                'board_id', NEW.board_id,
                'inviter_user_id', NEW.inviter_user_id,
                'status', NEW.status
            ),
            false,
            NOW()
        );
    END IF;

    -- Tạo notification cho inviter khi invitation được accept
    IF TG_OP = 'UPDATE' AND OLD.status != 'accept' AND NEW.status = 'accept' THEN
        INSERT INTO notifications (user_id, type, note, data, isread, createdat)
        VALUES (
            NEW.inviter_user_id,
            'INVITATION_ACCEPTED',
            'Your board invitation has been accepted',
            jsonb_build_object(
                'invitation_id', NEW.id,
                'board_id', NEW.board_id,
                'invited_user_id', NEW.invited_user_id,
                'status', NEW.status
            ),
            false,
            NOW()
        );

        -- Tự động thêm user vào board_members khi accept invitation
        INSERT INTO board_members (board_id, user_id, joined_at)
        VALUES (NEW.board_id, NEW.invited_user_id, NOW())
        ON CONFLICT (board_id, user_id) DO NOTHING;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tạo trigger cho INSERT (invitation mới)
CREATE OR REPLACE TRIGGER invitation_notification_trigger
    AFTER INSERT ON invitations
    FOR EACH ROW
    EXECUTE FUNCTION create_invitation_notification();

-- Tạo trigger cho UPDATE (khi status thay đổi)
CREATE OR REPLACE TRIGGER invitation_status_change_trigger
    AFTER UPDATE ON invitations
    FOR EACH ROW
    EXECUTE FUNCTION create_invitation_notification();

-- Trigger function để update thống kê khi card được hoàn thành
CREATE OR REPLACE FUNCTION update_card_stats()
RETURNS TRIGGER AS $$
BEGIN
    -- Nếu card được đánh dấu hoàn thành
    IF OLD.is_done = false AND NEW.is_done = true THEN
        -- Có thể thêm logic để update thống kê ở đây
        -- Ví dụ: update bảng statistics hoặc gửi notification

        -- Tạo notification cho owner của board
        INSERT INTO notifications (user_id, type, note, data, isread, createdat)
        SELECT
            b.owner_id,
            'CARD_COMPLETED',
            'A card has been completed in your board',
            jsonb_build_object(
                'card_id', NEW.id,
                'card_title', NEW.title,
                'board_id', c.board_id,
                'board_title', b.title
            ),
            false,
            NOW()
        FROM columns c
        JOIN boards b ON c.board_id = b.id
        WHERE c.id = NEW.column_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tạo trigger cho card completion
DROP TRIGGER IF EXISTS card_completion_trigger ON cards;
CREATE TRIGGER card_completion_trigger
    AFTER UPDATE ON cards
    FOR EACH ROW
    EXECUTE FUNCTION update_card_stats();

-- Trigger function để auto-update timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.modified_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Stored procedure để lấy thống kê board
CREATE OR REPLACE FUNCTION get_board_statistics(board_id_param UUID)
RETURNS TABLE (
    total_cards INTEGER,
    completed_cards INTEGER,
    pending_cards INTEGER,
    total_members INTEGER,
    completion_rate DECIMAL(5,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(c.id)::INTEGER as total_cards,
        COUNT(CASE WHEN c.is_done = true THEN 1 END)::INTEGER as completed_cards,
        COUNT(CASE WHEN c.is_done = false THEN 1 END)::INTEGER as pending_cards,
        (SELECT COUNT(*)::INTEGER FROM board_members bm WHERE bm.board_id = board_id_param) as total_members,
        CASE
            WHEN COUNT(c.id) = 0 THEN 0
            ELSE ROUND((COUNT(CASE WHEN c.is_done = true THEN 1 END) * 100.0 / COUNT(c.id)), 2)
        END as completion_rate
    FROM columns col
    LEFT JOIN cards c ON col.id = c.column_id
    WHERE col.board_id = board_id_param;
END;
$$ LANGUAGE plpgsql;

-- Stored procedure để clean up expired invitations
CREATE OR REPLACE FUNCTION cleanup_expired_invitations()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    -- Xóa các invitation pending quá 7 ngày
    DELETE FROM invitations
    WHERE status = 'pending'
    AND sent_at < NOW() - INTERVAL '7 days';

    GET DIAGNOSTICS deleted_count = ROW_COUNT;

    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;
