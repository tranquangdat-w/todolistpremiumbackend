package com.fsoft.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCommentRegistrationRequest {
    @NotNull(message = "card id cannot be null")
    private UUID cardId;
    @NotNull(message = "user id cannot be null")
    private UUID userId;
    @NotNull(message = "user avatar cannot be null")
    private String userAvatarUrl;
    @NotNull(message = "content cannot be null")
    private String content;
    @NotNull(message = "created date cannot be null")
    private Date createdAt;
}
