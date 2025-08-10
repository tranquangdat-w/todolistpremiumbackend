package com.fsoft.service;

import com.fsoft.model.BoardColumn;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

public interface ColumnService {
    public BoardColumn addNewColumn(String title,
                                    String description,
                                    Date createdAt,
                                    UUID boardId);

    public void deleteColumn(UUID id);

    public BoardColumn updateColumnDetails(UUID id, String title, String description, Date createdAt);

    public ArrayList<BoardColumn> getColumnByUserId(UUID userId);
}
