package com.fsoft.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "cards")
@ToString
public class Cards {
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id")
  private UUID cardId;

  @Column(name = "title", nullable = false)
  private String taskTitle;

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
  private BoardColumn column;
}
