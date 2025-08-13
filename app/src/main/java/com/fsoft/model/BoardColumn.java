package com.fsoft.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="columns")
public class BoardColumn {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "board_id")
    private Boards board;
}
