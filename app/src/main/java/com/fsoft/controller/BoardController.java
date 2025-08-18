package com.fsoft.controller;

import com.fsoft.dto.*;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
  private final BoardService boardService;

  @PostMapping
  public ResponseEntity<BoardDto> createBoard(
      @Valid @RequestBody CreateBoardDto createBoardDto,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();
    BoardDto createdBoard = boardService.createBoard(userId, createBoardDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard);
  }

  @PutMapping("/{boardId}")
  public ResponseEntity<Map<String, String>> updateBoard(
      @PathVariable UUID boardId,
      @Valid @RequestBody UpdateBoardDto updateBoardDto,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();
    boardService.updateBoard(boardId, userId, updateBoardDto);
    return ResponseEntity.ok(Map.of("message", "Update board successfully"));
  }

  @DeleteMapping("/{boardId}")
  public ResponseEntity<Map<String, String>> deleteBoard(
      @PathVariable UUID boardId,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();
    boardService.deleteBoard(boardId, userId);
    return ResponseEntity.ok(Map.of("message", "Board deleted successfully"));
  }

  @GetMapping
  public ResponseEntity<PageDto<BoardDto>> getBoardsByUserId(
      @AuthenticationPrincipal JwtPayload jwtPayload,
      Pageable pageable) {
    UUID userId = jwtPayload.getId();
    Page<BoardDto> page = boardService.getBoardsByUserId(userId, pageable);
    return ResponseEntity.ok(new PageDto<>(page));
  }

  @GetMapping("/{boardId}")
  public ResponseEntity<BoardDetailsDto> getBoardDetails(
      @PathVariable UUID boardId,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();
    BoardDetailsDto board = boardService.getBoardDetail(boardId, userId);

    return ResponseEntity.ok(board);
  }

  @GetMapping("/search")
  public ResponseEntity<Map<String, Object>> getBoardByKeyword(
          @AuthenticationPrincipal JwtPayload jwtPayload,
          @RequestParam(required = false) String search) {
    UUID owner_id = jwtPayload.getId();
    List<SearchBoardDto> searchBoardDtos = boardService.searchBoardByKeyword(owner_id, search);
    Map<String, Object> response = new HashMap<>();
    response.put("data", searchBoardDtos);
    response.put("status", "success");
    return ResponseEntity.ok(response);
  }
}
