package com.fsoft.controller;

import com.fsoft.dto.ColumnDto;
import com.fsoft.dto.ColumnRegistrationRequest;
import com.fsoft.dto.ColumnUpdateRequest;
import com.fsoft.model.BoardColumn;
import com.fsoft.model.Boards;
import com.fsoft.repository.BoardRepository;
import com.fsoft.security.jwt.JwtPayload;
import com.fsoft.service.ColumnService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/columns")
@AllArgsConstructor
public class ColumnController {
    @Autowired
    private final ColumnService columnService;

    @PostMapping("")
    public ResponseEntity<ColumnDto> addColumn(@RequestBody @Valid ColumnRegistrationRequest request) {
        ColumnDto boardColumn = columnService.addNewColumn(
                request.getTitle(),
                request.getDescription(),
                request.getCreatedAt(),
                request.getBoardId()
        );
        return new ResponseEntity<>(boardColumn, HttpStatus.OK);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable String columnId, @AuthenticationPrincipal JwtPayload user) {
        columnService.deleteColumn(columnId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumn> updateColumn(@PathVariable String columnId ,@RequestBody @Valid ColumnUpdateRequest request, @AuthenticationPrincipal JwtPayload user) {
        UUID userId = user.getId();
        BoardColumn updatedColumn = columnService.updateColumnDetails(
                columnId,
                request.getBoardId(),
                userId,
                request.getTitle(),
                request.getDescription(),
                request.getCreatedAt()
        );
        return new ResponseEntity<>(updatedColumn, HttpStatus.OK);
    }
}
