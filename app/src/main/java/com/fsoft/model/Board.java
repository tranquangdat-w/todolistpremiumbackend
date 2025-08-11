package com.fsoft.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boards")
public class Board {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @NotNull
  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private User user;

  @NotNull
  @Column(name = "created_at")
  private LocalDate createdAt;
}
