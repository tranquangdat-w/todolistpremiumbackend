package com.fsoft.repository;

import com.fsoft.model.Boards;
import com.fsoft.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Boards, UUID> {
  List<Boards> findByUser(User user);

  Page<Boards> findByUser_Id(UUID userId, Pageable pageable);

  Optional<Boards> findById(UUID id);
}

