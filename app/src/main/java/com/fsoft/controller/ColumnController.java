package com.fsoft.controller;

import com.fsoft.dto.ColumnRegistrationRequest;
import com.fsoft.dto.ColumnUpdateRequest;
import com.fsoft.model.Board;
import com.fsoft.model.BoardColumn;
import com.fsoft.service.ColumnService;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/columns")
@AllArgsConstructor
public class ColumnController {
    @Autowired
    private final ColumnService columnService;

    @PostMapping("")
    public ResponseEntity<BoardColumn> addColumn(@RequestBody @Valid ColumnRegistrationRequest request) {
        BoardColumn boardColumn = columnService.addNewColumn(
                request.getTitle(),
                request.getDescription(),
                request.getCreatedAt(),
                request.getBoardId()
        );
        return new ResponseEntity<>(boardColumn, HttpStatus.OK);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID columnId) {
        columnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("")
    public ResponseEntity<BoardColumn> updateColumn(@RequestBody @Valid ColumnUpdateRequest request) {
        BoardColumn updatedColumn = columnService.updateColumnDetails(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getCreatedAt()
        );
        return new ResponseEntity<>(updatedColumn, HttpStatus.OK);
    }
}
