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

    public void deleteColumn(String id);

    public BoardColumn updateColumnDetails(String id, String title, String description, Date createdAt);
}
