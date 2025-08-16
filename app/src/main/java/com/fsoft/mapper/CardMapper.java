package com.fsoft.mapper;

import java.util.LinkedList;
import java.util.stream.Collectors;

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
    cardDetailsDto.setDescription(card.getDescription());
    cardDetailsDto.setColumnId(card.getColumn().getId());

    if (card.getCardComments() != null) {
      cardDetailsDto.setComments(
          card.getCardComments()
              .stream()
              .map(CardCommentMapper::toCardCommentDetailsDto)
              .collect(Collectors.toList()));
    } else {
      cardDetailsDto.setComments(new LinkedList<>());
    }

    return cardDetailsDto;
  }
}
