package com.fsoft.repository;

import com.fsoft.model.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Cards, UUID> {
    @Override
    Optional<Cards> findById(UUID uuid);

    ArrayList<Cards> findByColumnId(String id);
}
