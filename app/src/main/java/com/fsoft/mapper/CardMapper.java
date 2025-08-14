package com.fsoft.mapper;

import com.fsoft.dto.CardDetailsDto;
import com.fsoft.model.Card;

public class CardMapper {
  private CardMapper() {
  }

  public static CardDetailsDto toCardDetailsDto(Card card) {
    if (card == null) {
      return null;
    }

    CardDetailsDto cardDetailsDto = new CardDetailsDto();
    cardDetailsDto.setId(card.getId());
    cardDetailsDto.setTitle(card.getTitle());
    cardDetailsDto.setIsDone(card.getIsDone());
    cardDetailsDto.setCreatedAt(card.getCreatedAt());
    cardDetailsDto.setDeadline(card.getDeadline());
    cardDetailsDto.setPosition(card.getPosition());
    cardDetailsDto.setCover(card.getCover());
    return cardDetailsDto;
  }
}
