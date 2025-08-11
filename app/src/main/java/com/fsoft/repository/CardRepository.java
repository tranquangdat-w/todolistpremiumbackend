package com.fsoft.repository;

import com.fsoft.model.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Cards, UUID> {
    Optional<Cards> findByCardIdAndBoardId(UUID cardId, UUID boardId);
}
