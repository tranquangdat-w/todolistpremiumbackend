package com.fsoft.service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Board;
import com.fsoft.model.BoardColumn;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ColumnServiceImpl implements ColumnService {

    BoardRepository boardRepository;
    ColumnRepository columnRepository;

    @Override
    public BoardColumn addNewColumn(String title, String description, Date createdAt, UUID boardId) {
        Board board = boardRepository.findById(boardId);
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setTitle(title);
        boardColumn.setDescription(description);
        boardColumn.setCreatedAt(createdAt);
        boardColumn.setBoard(board);
        columnRepository.save(boardColumn);
        return boardColumn;
    }

    @Override
    public void deleteColumn(String id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow();
        columnRepository.deleteById(id);
    }

    @Override
    public BoardColumn updateColumnDetails(String id, String title, String description, Date createdAt) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ApiException("Column not found", 404));;
        column.setTitle(title);
        column.setDescription(description);
        column.setCreatedAt(createdAt);
        columnRepository.save(column);
        return column;
    }
}
