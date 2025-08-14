package com.fsoft.service;

import com.fsoft.dto.ColumnDetailsDto;
import com.fsoft.dto.ColumnRegistrationRequest;
import com.fsoft.dto.ColumnUpdateRequest;
import com.fsoft.exceptions.ApiException;
import com.fsoft.mapper.ColumnMapper;
import com.fsoft.model.Board;
import com.fsoft.model.Columnn;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColumnService {
  private final ColumnRepository columnRepository;
  private final BoardRepository boardRepository;

  @Transactional
  public ColumnDetailsDto addNewColumn(UUID userId, ColumnRegistrationRequest request) {
    Board board = checkBoardExist(UUID.fromString(request.getBoardId()));

    checkPermisstionOfUser(board, userId);

    Columnn column = new Columnn();
    column.setTitle(request.getTitle());
    column.setBoard(board);
    column.setPosition(request.getPosition());
    column.setCreatedAt(new Date());

    columnRepository.save(column);

    return ColumnMapper.toColumnDetailsDto(column);
  }

  @Transactional
  public void updateColumn(
      UUID columnId,
      UUID userId,
      ColumnUpdateRequest columnUpdateRequest) {

    UUID boardId = UUID.fromString(columnUpdateRequest.getBoardId());

    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);
    checkBoardContainsColumn(board, columnId);

    Columnn column = columnRepository.findById(columnId)
        .orElseThrow(() -> new ApiException(
            "Column not found",
            HttpStatus.NOT_FOUND.value()));

    if (columnUpdateRequest.getTitle() != null) {
      column.setTitle(columnUpdateRequest.getTitle());
    }

    if (columnUpdateRequest.getPosition() != null) {
      column.setPosition(columnUpdateRequest.getPosition());
    }

    columnRepository.save(column);
  }

  @Transactional
  public void deleteColumn(UUID boardId, UUID columnId, UUID userId) {
    Board board = checkBoardExist(boardId);

    checkPermisstionOfUser(board, userId);

    checkBoardContainsColumn(board, columnId);

    columnRepository.deleteById(columnId);
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

  private Board checkBoardExist(UUID boardId) {
    return boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));
  }

  private void checkPermisstionOfUser(Board board, UUID userId) {
    if (!board.getOwner().getId().equals(userId)
        && !board.getMembers().stream().anyMatch(member -> member.getId().equals(userId))) {
      throw new ApiException("You don't have permission to do this action",
          HttpStatus.FORBIDDEN.value());
    }
  }
}
