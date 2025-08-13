package com.fsoft.repository;

import com.fsoft.model.CardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface CardCommentRepository extends JpaRepository<CardComment, UUID> {
    public ArrayList<CardComment> findByCardsCardId(UUID cardId);

    Optional<CardComment> findById(UUID cardCommentId);
}
