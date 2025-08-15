package com.fsoft.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;

@Entity
@Data
@Table(name = "cards")
public class Card {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "cover")
  private String cover;

  @Column(name = "is_done")
  private Boolean isDone;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "deadline")
  private Instant deadline;

  @Column(name = "position", nullable = false)
  private BigDecimal position;

  @ManyToOne
  @JoinColumn(name = "column_id", nullable = false)
  private Columnn column;
}
