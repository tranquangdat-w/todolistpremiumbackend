package com.fsoft.controller;

import com.fsoft.dto.CardDetailsDto;
import com.fsoft.dto.CardUpdateRequest;
import com.fsoft.dto.CreateCardRequest;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fsoft.utils.ImageValidator;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
  private final CardService cardService;

  @PostMapping
  public ResponseEntity<CardDetailsDto> createCard(
      @Valid @RequestBody CreateCardRequest request,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();

    CardDetailsDto createdCard = cardService.createCard(userId, request);

    return ResponseEntity.status(HttpStatus.OK).body(createdCard);
  }

  @PutMapping("/{cardId}")
  public ResponseEntity<CardDetailsDto> updateCard(
      @PathVariable String cardId,
      @Valid @RequestBody CardUpdateRequest request,
      @AuthenticationPrincipal JwtPayload user) {

    UUID userId = user.getId();

    CardDetailsDto result = cardService.updateCard(
        userId,
        UUID.fromString(cardId),
        request);

    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @DeleteMapping("/{cardId}")
  public ResponseEntity<Map<String, String>> deleteCard(
      @PathVariable UUID cardId,
      @RequestParam UUID boardId,
      @AuthenticationPrincipal JwtPayload jwtPayload) {

    cardService.deleteCard(boardId, cardId, jwtPayload.getId());
    return ResponseEntity.ok().body(Map.of("message", "Card deleted successfully"));
  }

  @GetMapping("/{cardId}")
  public ResponseEntity<CardDetailsDto> getCardDetails(
      @PathVariable String cardId,
      @RequestParam String boardId,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    CardDetailsDto result = cardService.getCardDetails(
        jwtPayload.getId(),
        UUID.fromString(boardId),
        UUID.fromString(cardId));

    return ResponseEntity.ok(result);
  }

  @PutMapping("/{cardId}/cover")
  public ResponseEntity<CardDetailsDto> updateCardCover(
      @PathVariable UUID cardId,
      @RequestParam UUID boardId,
      @RequestPart(value = "cover", required = false) MultipartFile coverFile,
      @AuthenticationPrincipal JwtPayload jwtPayload) {

    UUID userId = jwtPayload.getId();

    ImageValidator.validateAvatar(coverFile);

    CardDetailsDto updatedCard = cardService.updateCardCover(userId, boardId, cardId, coverFile);

    return ResponseEntity.status(HttpStatus.OK).body(updatedCard);
  }
}
