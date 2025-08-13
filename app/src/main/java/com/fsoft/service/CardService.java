package com.fsoft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsoft.dto.CardDto;
import com.fsoft.dto.CardUploadDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.BoardColumn;
import com.fsoft.model.Cards;
import com.fsoft.model.Boards;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
  private final CardRepository cardRepository;
  private final BoardRepository boardRepository;
  private final ColumnRepository columnRepository;

  public CardDto addCard(CardUploadDto newCard) {
    BoardColumn column = columnRepository.findById(newCard.getColumnId())
            .orElseThrow(() -> new ApiException("Column not found"));
    Cards cards = new Cards();

    cards.setTaskTitle(newCard.getTaskTitle());
    cards.setDeadline(newCard.getDeadline());
    cards.setDescription(newCard.getDescription());
    cards.setIsDone(newCard.getIsDone());
    cards.setCreatedAt(newCard.getCreatedAt());

    cards.setColumn(column);
    cardRepository.save(cards);
    System.out.println(cards);
    CardDto returnCard = new CardDto();

    returnCard.setCardID(cards.getCardId());
    returnCard.setColumnId(newCard.getColumnId());
    returnCard.setTaskTitle(newCard.getTaskTitle());
    returnCard.setDeadline(newCard.getDeadline());
    returnCard.setDescription(newCard.getDescription());
    returnCard.setIsDone(newCard.getIsDone());
    returnCard.setCreatedAt(newCard.getCreatedAt());
    returnCard.setDeadline(newCard.getDeadline());

    return returnCard;
  }

  public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
    Boards board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
      throw new ApiException("You don't have permission to delete this card", HttpStatus.FORBIDDEN.value());
    }

    Cards card = cardRepository.findById(cardId)
        .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));

    cardRepository.delete(card);
  }
}
