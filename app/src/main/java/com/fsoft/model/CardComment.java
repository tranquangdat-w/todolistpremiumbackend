package com.fsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardComment {
    @Id
    @GeneratedValue()
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    @NotNull
    private Cards cards;
    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;
    @Column(name = "user_avatar_url")
    private String userAvatarUrl;
    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private Date createdAt;
}
