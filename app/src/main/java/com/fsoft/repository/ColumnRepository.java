package com.fsoft.repository;

import com.fsoft.model.Columns;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<Columns, String> {
    Optional<Columns> findByColumnIdAndBoardId(String columnId, UUID boardId);
}
