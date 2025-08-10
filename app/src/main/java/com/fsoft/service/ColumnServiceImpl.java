package com.fsoft.service;

import com.fsoft.exceptions.ApiException;
import com.fsoft.model.Board;
import com.fsoft.model.BoardColumn;
import com.fsoft.model.Task;
import com.fsoft.repository.BoardRepository;
import com.fsoft.repository.ColumnRepository;
import com.fsoft.repository.TaskRepository;
import jakarta.persistence.Column;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ColumnServiceImpl implements ColumnService {

    BoardRepository boardRepository;
    ColumnRepository columnRepository;
    TaskRepository taskRepository;

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
    public void deleteColumn(UUID id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow();
        ArrayList<Task> tasks = taskRepository.findByColumnId(column.getId());
        taskRepository.deleteAll(tasks);
        columnRepository.deleteById(id);
    }

    @Override
    @Transactional
    public BoardColumn updateColumnDetails(UUID id, String title, String description, Date createdAt) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ApiException("Column not found", 404));;
        column.setTitle(title);
        column.setDescription(description);
        column.setCreatedAt(createdAt);
        columnRepository.save(column);
        return column;
    }

    @Override
    public ArrayList<BoardColumn> getColumnByUserId(UUID userId) {
        return null;
    }
}
