package com.fsoft.model;

import lombok.Data;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "cards")
public class Card {
  @Id
  @Column(name = "id")
  private UUID cardId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "is_done")
  private Boolean isDone;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "deadline")
  private Date deadline;

  @ManyToOne
  @JoinColumn(name = "column_id", nullable = false)
  private Columnn column;
}
