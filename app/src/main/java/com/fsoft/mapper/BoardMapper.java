package com.fsoft.mapper;

import com.fsoft.dto.BoardDetailsDto;
import com.fsoft.dto.BoardDto;
import com.fsoft.model.Board;
import com.fsoft.mapper.UserMapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class BoardMapper {
  public static BoardDto toBoardDto(Board board) {
    BoardDto boardDto = new BoardDto();
    boardDto.setId(board.getId());
    boardDto.setTitle(board.getTitle());
    boardDto.setDescription(board.getDescription());
    boardDto.setCreatedAt(board.getCreatedAt());
    return boardDto;
  }

  public static BoardDetailsDto toBoardDetailsDto(Board board) {
    BoardDetailsDto boardDetailsDto = new BoardDetailsDto();
    boardDetailsDto.setId(board.getId());
    boardDetailsDto.setTitle(board.getTitle());
    boardDetailsDto.setDescription(board.getDescription());
    boardDetailsDto.setCreatedAt(board.getCreatedAt());

    if (board.getColumns() != null) {
      boardDetailsDto.setColumns(
          board.getColumns()
              .stream()
              .map(ColumnMapper::toColumnDetailsDto)
              .collect(Collectors.toList()));
    } else {
      boardDetailsDto.setColumns(new LinkedList<>());
    }

    if (board.getMembers() != null) {
      boardDetailsDto.setMembers(
          board.getMembers()
              .stream()
              .map(UserMapper::toUserDto)
              .collect(Collectors.toSet()));
    } else {
      boardDetailsDto.setMembers(new HashSet<>());
    }

    boardDetailsDto.setOwner(UserMapper.toUserDto(board.getOwner()));
    return boardDetailsDto;
  }
}
