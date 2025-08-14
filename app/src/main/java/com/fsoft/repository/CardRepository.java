package com.fsoft.repository;

import com.fsoft.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    @Query("SELECT MAX(c.position) FROM Card c WHERE c.column.id = :columnnId")
    BigDecimal findMaxPositionByColumnnId(@Param("columnnId") UUID columnnId);
}
