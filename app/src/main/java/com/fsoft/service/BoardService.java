package com.fsoft.service;

import com.fsoft.dto.BoardDetailsDto;
import com.fsoft.dto.BoardDto;
import com.fsoft.dto.CreateBoardDto;
import com.fsoft.dto.UpdateBoardDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Board;
import com.fsoft.model.User;
import com.fsoft.repository.BoardRepository;
import com.fsoft.mapper.BoardMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
  private final BoardRepository boardRepository;

  @Transactional
  public BoardDto createBoard(UUID userId, CreateBoardDto createBoardDto) {
    Board board = new Board();
    User user = new User();
    user.setId(userId);

    if (createBoardDto.getDescription() != null) {
      board.setDescription(createBoardDto.getDescription());
    }

    board.setTitle(createBoardDto.getTitle());
    board.setOwner(user);
    board.setCreatedAt(Instant.now());

    Board createdBoard = boardRepository.save(board);

    return BoardMapper.toBoardDto(createdBoard);
  }

  @Transactional
  public void updateBoard(UUID boardId, UUID userId, UpdateBoardDto updateBoardDto) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

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
    Board board = checkBoardExist(boardId);

    if (!board.getOwner().getId().equals(userId)) {
      throw new ApiException("You don't have permission to delete this board", HttpStatus.FORBIDDEN.value());
    }

    boardRepository.delete(board);
  }

  public BoardDetailsDto getBoardDetail(UUID boardId, UUID userId) {
    Board board = checkBoardExist(boardId);
    checkPermisstionOfUser(board, userId);

    return BoardMapper.toBoardDetailsDto(board);
  }

  public Page<BoardDto> getBoardsByUserId(UUID userId, Pageable pageable) {
    return boardRepository
        .findByOwnerOrMember(userId, pageable)
        .map(BoardMapper::toBoardDto);
  }

  private Board checkBoardExist(UUID boardId) {
    return boardRepository.findById(boardId)
        .orElseThrow(() -> new ApiException("Board not found", HttpStatus.NOT_FOUND.value()));
  }

  private void checkPermisstionOfUser(Board board, UUID userId) {
    if (!board.getOwner().getId().equals(userId)
        && !board.getMembers().stream().anyMatch(member -> member.getId().equals(userId))) {
      throw new ApiException("You don't have permission to view this board",
          HttpStatus.FORBIDDEN.value());
    }
  }
}
