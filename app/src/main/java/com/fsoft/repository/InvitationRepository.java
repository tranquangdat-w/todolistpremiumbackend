package com.fsoft.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fsoft.model.Boards;
import com.fsoft.model.Invitation;
import com.fsoft.model.User;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findAllByInvitedUser(User user);
    List<Invitation> findAllByInvitedUserAndBoardAndStatus(User invitedUser, Boards board, String status);
    Optional<Invitation> findByInvitedUserAndBoardAndStatus(User invitedUser, Boards board, String status);
}
