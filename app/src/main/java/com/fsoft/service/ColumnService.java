package com.fsoft.service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Columnn;
import com.fsoft.model.Board;
import com.fsoft.repository.ColumnRepository;
import com.fsoft.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ColumnService {
  private final ColumnRepository columnRepository;
  private final BoardRepository boardRepository;

  public void deleteColumn(UUID boardId, String columnId, UUID userId) {
    Board board = boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));

    if (!board.getUser().getId().equals(userId)) {
      throw new ApiException("You don't have permission to delete this column", HttpStatus.FORBIDDEN.value());
    }

    Columnn column = columnRepository.findById(columnId)
        .orElseThrow(() -> new ApiException("Column not found", HttpStatus.NOT_FOUND.value()));

    // Check if column belongs to the specified board
    if (!column.getBoard().getId().equals(boardId)) {
      throw new ApiException("Column does not belong to the specified board", HttpStatus.BAD_REQUEST.value());
    }

    columnRepository.delete(column);
  }
}
