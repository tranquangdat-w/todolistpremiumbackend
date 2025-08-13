package com.fsoft.controller;

import com.fsoft.dto.CardCommentDto;
import com.fsoft.dto.CardCommentRegistrationRequest;
import com.fsoft.dto.CardCommentUpdateRequest;
import com.fsoft.repository.CardCommentRepository;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.CardCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards/comments")
public class CardCommentController {
    @Autowired
    private final CardCommentService cardCommentService;

    @PostMapping("/")
    public ResponseEntity<CardCommentDto> addCardComment(@RequestBody CardCommentRegistrationRequest request) {
        CardCommentDto dto = cardCommentService.addCardComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<List<CardCommentDto>> getCardCommentsById(@PathVariable UUID cardId, @AuthenticationPrincipal JwtPayload user) {
        List<CardCommentDto> dto = cardCommentService.getCardComments(cardId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/{cardCommentId}")
    public ResponseEntity<Void> deleteCardComment(@PathVariable UUID cardCommentId, @AuthenticationPrincipal JwtPayload user) {
        cardCommentService.deleteCardComment(cardCommentId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cardCommentId}")
    public ResponseEntity<CardCommentDto> updateCardComment(@PathVariable UUID cardCommentId, @RequestBody @Valid CardCommentUpdateRequest request, @AuthenticationPrincipal JwtPayload user) {
        CardCommentDto cardCommentDto = cardCommentService.updateCardComment(request, user.getId(), cardCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(cardCommentDto);
    }
}
