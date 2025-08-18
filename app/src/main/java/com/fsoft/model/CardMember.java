package com.fsoft.model;

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
@Table(name = "card_members")
@IdClass(CardMemberId.class)
public class CardMember {
  @Id
  @Column(name = "card_id")
  private UUID cardId;

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @ManyToOne
  @JoinColumn(name = "card_id", insertable = false, updatable = false)
  private Card card;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;
}
