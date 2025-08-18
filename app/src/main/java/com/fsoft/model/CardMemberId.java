package com.fsoft.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CardMemberId implements Serializable {
  private UUID cardId;
  private UUID userId;
}
