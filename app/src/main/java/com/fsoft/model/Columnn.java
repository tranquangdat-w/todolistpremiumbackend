package com.fsoft.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "columns")
public class Columnn {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "position", nullable = false)
  private BigDecimal position;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

  @OneToMany(mappedBy = "column")
  private List<Card> cards;
}
