package com.fsoft.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "board_members")
@IdClass(BoardMemberId.class)  // For composite primary key
public class BoardMember {
    @Id
    @Column(name = "board_id")
    private UUID boardId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    // If you want relationships with Board and User entities
    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
