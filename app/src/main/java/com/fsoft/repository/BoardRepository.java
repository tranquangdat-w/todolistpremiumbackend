package com.fsoft.repository;

import com.fsoft.model.Board;
import com.fsoft.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
  List<Board> findByUser(User user);

  Page<Board> findByUser_Id(UUID userId, Pageable pageable);
}
