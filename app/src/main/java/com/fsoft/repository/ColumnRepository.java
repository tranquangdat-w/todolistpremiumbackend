package com.fsoft.repository;

import com.fsoft.model.Columnn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<Columnn, UUID> {
  ArrayList<Columnn> findByBoardId(UUID boardId);

  Optional<Columnn> findById(UUID id);

  void deleteById(UUID columnId);

  Optional<Columnn> findByIdAndBoardId(UUID columnId, UUID boardId);
}
