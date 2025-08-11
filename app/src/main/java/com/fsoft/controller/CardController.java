package com.fsoft.controller;

import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
  private final CardService cardService;

  @DeleteMapping("/{cardId}")
  public ResponseEntity<Map<String, String>> deleteCard(
      @PathVariable UUID cardId,
      @RequestParam UUID boardId,
      @AuthenticationPrincipal JwtPayload jwtPayload) {

    cardService.deleteCard(boardId, cardId, jwtPayload.getId());
    return ResponseEntity.ok(Map.of("message", "Card deleted successfully"));
  }
}
