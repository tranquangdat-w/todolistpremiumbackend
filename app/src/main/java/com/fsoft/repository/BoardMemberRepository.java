package com.fsoft.repository;

import com.fsoft.model.BoardMember;
import com.fsoft.model.BoardMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BoardMemberRepository extends JpaRepository<BoardMember, BoardMemberId> {
}
