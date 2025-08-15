package com.fsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cards")
public class Cards {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private UUID id;

  @NotNull
  @Column(name = "title")
  private String title;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @NotNull
  @Builder.Default
  @Column(name = "is_done")
  private Boolean isDone = false;

  @NotNull
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "deadline")
  private LocalDateTime deadline;

  @NotNull
  @Column(name = "position")
  private BigDecimal position;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "column_id")
  private Columns column;

  @Column(name = "cover", columnDefinition = "text")
  private String cover;
}
