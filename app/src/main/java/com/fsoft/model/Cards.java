package com.fsoft.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "cards")
public class Cards {

    @Id
    @Column(name = "card_id")
    private UUID cardId;

    @Column(name = "task_title", nullable = false)
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
