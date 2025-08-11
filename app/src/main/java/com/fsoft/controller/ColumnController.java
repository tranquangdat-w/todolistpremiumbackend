package com.fsoft.controller;

import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/columns")
public class ColumnController {
    private final ColumnService columnService;

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Map<String, String>> deleteColumn(
            @PathVariable String columnId,
            @RequestParam UUID boardId,
            @AuthenticationPrincipal JwtPayload jwtPayload) {

        columnService.deleteColumn(boardId, columnId, jwtPayload.getId());
        return ResponseEntity.ok(Map.of("message", "Column deleted successfully"));
    }
}
