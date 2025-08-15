package com.fsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "board_members")
public class BoardMembers {

  @EmbeddedId
  private BoardMembersId id;

  @NotNull
  @Column(name = "joined_at")
  private LocalDateTime joinedAt;

  @ManyToOne
  @MapsId("boardId")
  @JoinColumn(name = "board_id")
  private Boards board;

  @ManyToOne
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private User user;

  @Embeddable
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BoardMembersId {
    @Column(name = "board_id")
    private UUID boardId;

    @Column(name = "user_id")
    private UUID userId;
  }
}
