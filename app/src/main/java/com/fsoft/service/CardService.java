package com.fsoft.service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Card;
import com.fsoft.model.Board;
import com.fsoft.repository.CardRepository;
import com.fsoft.repository.BoardRepository;
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

  public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
      throw new ApiException("You don't have permission to delete this card", HttpStatus.FORBIDDEN.value());
    }

    Card card = cardRepository.findById(cardId)
        .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));

    cardRepository.delete(card);
  }
}
