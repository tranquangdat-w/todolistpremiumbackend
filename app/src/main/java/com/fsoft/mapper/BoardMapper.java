package com.fsoft.mapper;

import com.fsoft.dto.BoardDto;
import com.fsoft.model.Board;

public class BoardMapper {
  public static BoardDto toBoardDto(Board board) {
    BoardDto boardDto = new BoardDto();
    boardDto.setId(board.getId());
    boardDto.setTitle(board.getTitle());
    boardDto.setDescription(board.getDescription());
    boardDto.setCreatedAt(board.getCreatedAt());
    return boardDto;
  }
}
