package com.fsoft.repository;

import com.fsoft.model.Invitation;
import com.fsoft.model.User;
import com.fsoft.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    // Tìm tất cả invitation của một user
    List<Invitation> findAllByInvitedUser(User invitedUser);

    // Tìm invitation theo user, board và status
    List<Invitation> findAllByInvitedUserAndBoardAndStatus(User invitedUser, Board board, Invitation.InvitationStatus status);

    // Tìm invitation theo boardId và invitedUserId
    Optional<Invitation> findByBoardIdAndInvitedUserId(UUID boardId, UUID invitedUserId);

    // Tìm tất cả invitation của một user theo ID
    List<Invitation> findByInvitedUserIdOrderBySentAtDesc(UUID invitedUserId);

    // Tìm tất cả invitation mà user đã gửi
    List<Invitation> findByInviterUserIdOrderBySentAtDesc(UUID inviterUserId);

    // Tìm invitation theo status
    List<Invitation> findByStatusOrderBySentAtDesc(Invitation.InvitationStatus status);

    // Tìm invitation theo boardId
    List<Invitation> findByBoardIdOrderBySentAtDesc(UUID boardId);

    // Kiểm tra xem đã có invitation chưa - Fixed PostgreSQL parameter binding
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.boardId = :boardId AND i.invitedUserId = :invitedUserId AND i.status = :status")
    boolean existsPendingInvitation(@Param("boardId") UUID boardId, @Param("invitedUserId") UUID invitedUserId, @Param("status") Invitation.InvitationStatus status);

    // Kiểm tra xem user đã là member của board chưa (accepted invitation)
    @Query("SELECT COUNT(i) > 0 FROM Invitation i WHERE i.boardId = :boardId AND i.invitedUserId = :invitedUserId AND i.status = 'ACCEPT'")
    boolean isUserAlreadyMember(@Param("boardId") UUID boardId, @Param("invitedUserId") UUID invitedUserId);
}
