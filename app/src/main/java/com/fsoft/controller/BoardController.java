package com.fsoft.controller;

import com.fsoft.dto.BoardDto;
import com.fsoft.dto.CreateBoardDto;
import com.fsoft.dto.UpdateBoardDto;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import com.fsoft.dto.PageDto;
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
    public ResponseEntity<Map<String, String>> createBoard(
            @Valid @RequestBody CreateBoardDto createBoardDto,
            @AuthenticationPrincipal JwtPayload jwtPayload) {
        UUID userId = jwtPayload.getId();
        boardService.createBoard(userId, createBoardDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Create new board successfully"));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoardDetail(
            @PathVariable UUID boardId,
            @AuthenticationPrincipal JwtPayload jwtPayload) {
        UUID userId = jwtPayload.getId();
        BoardDto board = boardService.getBoardDetail(boardId, userId);
        return ResponseEntity.ok(board);
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
}
