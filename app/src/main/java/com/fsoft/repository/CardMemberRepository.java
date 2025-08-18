package com.fsoft.repository;

import com.fsoft.model.CardMember;
import com.fsoft.model.CardMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardMemberRepository extends JpaRepository<CardMember, CardMemberId> {
}
