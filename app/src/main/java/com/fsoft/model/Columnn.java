package com.fsoft.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "columns")
public class Columnn {
  @Id
  @Column(name = "id")
  private UUID columnId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;
}
