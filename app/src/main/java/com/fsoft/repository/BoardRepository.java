package com.fsoft.repository;

import com.fsoft.model.Boards;
import com.fsoft.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Boards, UUID> {
  List<Boards> findByUser(User user);

  Page<Boards> findByUser_Id(UUID userId, Pageable pageable);

  Optional<Boards> findById(UUID id);

  @Query(value = """
    SELECT id, title
    FROM boards b
    WHERE b.owner_id = :owner_id
      AND LOWER(b.title) LIKE '%' || LOWER(:keyword) || '%'
    """, nativeQuery = true)
  List<Boards> findByBoardNameContaining(@Param("owner_id") UUID owner_id, @Param("keyword") String keyword);
}

