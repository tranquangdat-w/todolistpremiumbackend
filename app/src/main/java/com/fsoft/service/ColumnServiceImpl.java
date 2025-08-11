package com.fsoft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsoft.dto.ColumnDto;
import com.fsoft.exceptions.ApiException;
import com.fsoft.model.BoardColumn;
import com.fsoft.model.Boards;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    BoardRepository boardRepository;
    @Autowired
    ColumnRepository columnRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public ColumnDto addNewColumn(String title, String description, Date createdAt, UUID boardId) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow();
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setTitle(title);
        boardColumn.setDescription(description);
        boardColumn.setCreatedAt(createdAt);
        boardColumn.setBoard(board);
        boardColumn.setId(UUID.randomUUID().toString());
        columnRepository.save(boardColumn);
        ColumnDto columnDto =  objectMapper.convertValue(boardColumn, ColumnDto.class);
        columnDto.setBoardId(board.getId());
        return columnDto;
    }

    @Override
    public void deleteColumn(String id, UUID userId) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ApiException("Cannot find column", 404));
        System.out.println(column);
        Boards board = column.getBoard();
        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("You are not authorized to delete this column", 401);
        }
        columnRepository.deleteById(id);
    }

    @Override
    public BoardColumn updateColumnDetails(
            String id,
            UUID boardId,
            UUID userId,
            String title,
            String description,
            Date createdAt
    ) {
        Boards board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException("Board not found", 404));

        if (!board.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized: User does not own this board", 403);
        }

        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ApiException("Column not found", 404));

        if (!column.getBoard().getId().equals(boardId)) {
            throw new ApiException("Board Id doesn't match", 404);
        }

        // Cập nhật dữ liệu
        column.setTitle(title);
        column.setDescription(description);
        column.setCreatedAt(createdAt);

        columnRepository.save(column);
        return column;
    }
}
