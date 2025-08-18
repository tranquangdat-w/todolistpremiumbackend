package com.fsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "card_comments")
public class CardComment {
  @Id()
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @ManyToOne()
  @JoinColumn(name = "card_id", referencedColumnName = "id")
  @NotNull
  private Card card;

  @ManyToOne()
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @NotNull
  private User user;

  @Column(name = "content")
  private String content;

  @Column(name = "created_at")
  private Instant createdAt;
}
