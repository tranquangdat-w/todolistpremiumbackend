package com.fsoft.controller;

import com.fsoft.dto.ColumnDetailsDto;
import com.fsoft.dto.ColumnRegistrationRequest;
import com.fsoft.dto.ColumnUpdateRequest;
import com.fsoft.dto.DeleteColumnRequest;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.ColumnService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/columns")
@AllArgsConstructor
public class ColumnController {
  @Autowired
  private final ColumnService columnService;

  @PostMapping
  public ResponseEntity<ColumnDetailsDto> addColumn(
      @RequestBody @Valid ColumnRegistrationRequest request,
      @AuthenticationPrincipal JwtPayload jwtPayload) {
    UUID userId = jwtPayload.getId();

    ColumnDetailsDto boardColumn = columnService.addNewColumn(userId, request);

    return new ResponseEntity<>(boardColumn, HttpStatus.OK);
  }

  @DeleteMapping("/{columnId}")
  public ResponseEntity<Map<String, String>> deleteColumn(
      @PathVariable String columnId,
      @AuthenticationPrincipal JwtPayload user,
      @Valid @RequestBody DeleteColumnRequest request) {

    UUID boardId = UUID.fromString(request.getBoardId());

    columnService.deleteColumn(boardId, UUID.fromString(columnId), user.getId());

    return ResponseEntity.status(HttpStatus.OK).body(Map.of("Message", "deleted column"));
  }

  @PutMapping("/{columnId}")
  public ResponseEntity<Map<String, String>> updateColumn(
      @PathVariable String columnId,
      @Valid @RequestBody ColumnUpdateRequest request,
      @AuthenticationPrincipal JwtPayload user) {

    UUID userId = user.getId();

    columnService.updateColumn(
        UUID.fromString(columnId),
        userId,
        request);

    return ResponseEntity.status(HttpStatus.OK).body(Map.of("Message", "Updated column"));
  }
}
