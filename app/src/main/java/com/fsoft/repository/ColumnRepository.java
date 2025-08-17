package com.fsoft.repository;

import com.fsoft.model.Columnn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<Columnn, UUID> {
  ArrayList<Columnn> findByBoardId(UUID boardId);

  Optional<Columnn> findById(UUID id);

  void deleteById(UUID columnId);

  Optional<Columnn> findByIdAndBoardId(UUID columnId, UUID boardId);

  @Query("SELECT MAX(c.position) FROM Columnn c WHERE c.board.id = :boardId")
  BigDecimal findMaxPositionByBoardId(@Param("boardId") UUID boardId);
}
