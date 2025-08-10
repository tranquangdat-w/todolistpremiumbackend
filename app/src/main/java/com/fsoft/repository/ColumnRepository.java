package com.fsoft.repository;

import com.fsoft.model.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<BoardColumn, String> {
    ArrayList<BoardColumn> findByBoardId(UUID boardId);

    Optional<BoardColumn> findById(String id);

    void deleteById(String columnId);
}
