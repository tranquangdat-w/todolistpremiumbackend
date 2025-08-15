package com.fsoft.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardStatisticsDto {

    @JsonProperty("total_cards")
    private Integer totalCards;

    @JsonProperty("completed_cards")
    private Integer completedCards;

    @JsonProperty("pending_cards")
    private Integer pendingCards;

    @JsonProperty("total_members")
    private Integer totalMembers;

    @JsonProperty("completion_rate")
    private BigDecimal completionRate;
}
