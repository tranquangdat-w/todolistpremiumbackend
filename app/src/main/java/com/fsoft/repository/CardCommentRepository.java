package com.fsoft.repository;

import com.fsoft.model.CardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CardCommentRepository extends JpaRepository<CardComment, UUID> {
}
