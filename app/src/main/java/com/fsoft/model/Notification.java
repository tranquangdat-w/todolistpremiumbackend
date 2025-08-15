package com.fsoft.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @NotNull
    @Column(name = "note")
    private String note;

    @Column(name = "data")
    @JdbcTypeCode(SqlTypes.JSON)
    private String data;

    @NotNull
    @Builder.Default
    @Column(name = "isread")
    private Boolean isRead = false;

    @NotNull
    @CreatedDate
    @Column(name = "createdat", nullable = false)
    private LocalDateTime createdAt;
}
