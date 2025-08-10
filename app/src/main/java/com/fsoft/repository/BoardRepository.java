package com.fsoft.repository;

import com.fsoft.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Board findById(UUID id);

    void deleteById(UUID id);
}
