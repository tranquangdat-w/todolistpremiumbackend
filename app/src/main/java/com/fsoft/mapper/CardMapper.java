package com.fsoft.mapper;

import com.fsoft.dto.CardDetailsDto;
import com.fsoft.model.Card;

public class CardMapper {
  public static CardDetailsDto toCardDetailsDto(Card card) {
    CardDetailsDto cardDetailsDto = new CardDetailsDto();
    cardDetailsDto.setId(card.getId());
    cardDetailsDto.setTitle(card.getTitle());
    cardDetailsDto.setIsDone(card.getIsDone());
    cardDetailsDto.setCreatedAt(card.getCreatedAt());
    cardDetailsDto.setDeadline(card.getDeadline());
    cardDetailsDto.setPosition(card.getPosition());
    return cardDetailsDto;
  }
}
