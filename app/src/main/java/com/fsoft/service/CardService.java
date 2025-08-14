package com.fsoft.service;

import com.fsoft.dto.CardDetailsDto;
import com.fsoft.dto.CardUpdateRequest;
import com.fsoft.dto.CreateCardRequest;
import com.fsoft.exceptions.ApiException;
import com.fsoft.mapper.CardMapper;
import com.fsoft.model.Board;
import com.fsoft.model.Card;
import com.fsoft.model.Columnn;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
  private final CardRepository cardRepository;
  private final BoardRepository boardRepository;

  @Transactional
  public CardDetailsDto createCard(UUID userId, CreateCardRequest request) {
    UUID boardId = UUID.fromString(request.getBoardId());
    UUID columnId = UUID.fromString(request.getColumnId());
    Board board = checkBoardExist(boardId);

    checkPermisstionOfUser(board, userId);
    checkBoardContainsColumn(board, columnId);

    Columnn column = new Columnn();
    column.setId(columnId);

    Card card = new Card();
    card.setTitle(request.getTitle());
    card.setColumn(column);
    card.setIsDone(false);
    card.setPosition(request.getPosition());
    card.setCreatedAt(new Date());

    cardRepository.save(card);

    return CardMapper.toCardDetailsDto(card);
  }

  @Transactional
  public void updateCard(UUID userId, UUID cardId, CardUpdateRequest request) {
    UUID boardId = UUID.fromString(request.getBoardId());

    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    Card card = checkCardExist(cardId);

    if (request.getColumnId() != null) {
      Columnn column = new Columnn();
      column.setId(UUID.fromString(request.getColumnId()));
      card.setColumn(column);
    }

    if (request.getTitle() != null) {
      card.setTitle(request.getTitle());
    }

    if (request.getPosition() != null) {
      card.setPosition(request.getPosition());
    }

    cardRepository.save(card);
  }

  // public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
  // Board board = boardRepository.findById(boardId)
  // .orElseThrow(() -> new ApiException("Board not found",
  // HttpStatus.NOT_FOUND.value()));
  //
  // if (!board.getUser().getId().equals(userId)) {
  // throw new ApiException("You don't have permission to delete this card",
  // HttpStatus.FORBIDDEN.value());
  // }
  //
  // Card card = cardRepository.findById(cardId)
  // .orElseThrow(() -> new ApiException("Card not found",
  // HttpStatus.NOT_FOUND.value()));
  //
  // cardRepository.delete(card);
  // }
  //
  private Card checkCardExist(UUID cardId) {
    return cardRepository.findById(cardId)
        .orElseThrow(() -> new ApiException("Card not found", HttpStatus.NOT_FOUND.value()));
  }

  private void checkBoardContainsColumn(Board board, UUID columnId) {
    boolean exists = board.getColumns().stream()
        .anyMatch(column -> column.getId().equals(columnId));

    if (!exists) {
      throw new ApiException(
          "Column does not belong to the specified board",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  private void checkPermisstionOfUser(Board board, UUID userId) {
    if (!board.getOwner().getId().equals(userId)
        && !board.getMembers().stream().anyMatch(member -> member.getId().equals(userId))) {
      throw new ApiException("You don't have permission to view this board",
          HttpStatus.FORBIDDEN.value());
    }
  }

  private Board checkBoardExist(UUID boardId) {
    return boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));
  }
}
