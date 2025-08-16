package com.fsoft.controller;

import com.fsoft.dto.CardCommentDetailsDto;
import com.fsoft.dto.CreateCardCommentDto;
import com.fsoft.dto.UpdateCardCommentDto;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.CardCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards/comments")
public class CardCommentController {

    private final CardCommentService cardCommentService;

    @PostMapping
    public ResponseEntity<CardCommentDetailsDto> createComment(
            @RequestBody CreateCardCommentDto createDto,
            @AuthenticationPrincipal JwtPayload payload) {
        CardCommentDetailsDto newComment = cardCommentService.createComment(createDto, payload);
        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CardCommentDetailsDto> updateComment(
            @PathVariable UUID commentId,
            @RequestBody UpdateCardCommentDto updateDto,
            @AuthenticationPrincipal JwtPayload payload) {
        CardCommentDetailsDto updatedComment = cardCommentService.updateComment(commentId, updateDto, payload);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal JwtPayload payload) {
        cardCommentService.deleteComment(commentId, payload);
        return ResponseEntity.noContent().build();
    }
}
