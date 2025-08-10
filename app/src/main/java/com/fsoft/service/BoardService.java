package com.fsoft.service;

import com.fsoft.dto.BoardDto;
import com.fsoft.dto.CreateBoardDto;
import com.fsoft.dto.UpdateBoardDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Boards;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;
import com.fsoft.repository.CardRepository;
import com.fsoft.model.Columns;
import com.fsoft.model.Cards;
import com.fsoft.mapper.BoardMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {
  private final BoardRepository boardRepository;
  private final ColumnRepository columnRepository;
  private final CardRepository cardRepository;

  @Transactional
  public void createBoard(UUID userId, CreateBoardDto createBoardDto) {
    Boards board = new Boards();

    User user = new User();
    user.setId(userId);

    if (createBoardDto.getDescription() != null) {
      board.setDescription(createBoardDto.getDescription());
    }

    board.setTitle(createBoardDto.getTitle());
    board.setUser(user);
    board.setCreatedAt(LocalDate.now());

    boardRepository.save(board);
  }

  @Transactional
  public void updateBoard(UUID boardId, UUID userId, UpdateBoardDto updateBoardDto) {
    Boards board = boardRepository.findById(boardId).orElseThrow(
        () -> new ApiException(
            "Board not found with id: " + boardId,
            HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
      throw new ApiException(
          "You can't update this board " + boardId,
          HttpStatus.NOT_FOUND.value());
    }

    if (updateBoardDto.getTitle() != null) {
      board.setTitle(updateBoardDto.getTitle());
    }

    if (updateBoardDto.getDescription() != null) {
      board.setDescription(updateBoardDto.getDescription());
    }

    boardRepository.save(board);
  }

  @Transactional
  public void deleteBoard(UUID boardId, UUID userId) {
    Boards board = boardRepository.findById(boardId).orElseThrow(
        () -> new ApiException("Board not found with id: " + boardId, HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
        throw new ApiException("Unauthorized to delete this board", HttpStatus.FORBIDDEN.value());
    }

    boardRepository.delete(board);
  }

  @Transactional
  public void deleteColumn(UUID boardId, String columnId, UUID userId) {
    Boards board = boardRepository.findById(boardId).orElseThrow(
        () -> new ApiException("Board not found with id: " + boardId, HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
        throw new ApiException("Unauthorized to delete this column", HttpStatus.FORBIDDEN.value());
    }

    Columns column = columnRepository.findByColumnIdAndBoardId(columnId, boardId).orElseThrow(
        () -> new ApiException("Column not found with id: " + columnId, HttpStatus.NOT_FOUND.value()));

    columnRepository.delete(column);
  }

  @Transactional
  public void deleteCard(UUID boardId, UUID cardId, UUID userId) {
    Boards board = boardRepository.findById(boardId).orElseThrow(
        () -> new ApiException("Board not found with id: " + boardId, HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
        throw new ApiException("Unauthorized to delete this card", HttpStatus.FORBIDDEN.value());
    }

    Cards card = cardRepository.findByCardIdAndBoardId(cardId, boardId).orElseThrow(
        () -> new ApiException("Card not found with id: " + cardId, HttpStatus.NOT_FOUND.value()));

    cardRepository.delete(card);
  }

  public Page<BoardDto> getBoardsByUserId(UUID userId, Pageable pageable) {
    return boardRepository.findByUser_Id(userId, pageable).map(BoardMapper::toBoardDto);
  }
}

