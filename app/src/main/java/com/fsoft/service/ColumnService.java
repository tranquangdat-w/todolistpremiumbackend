package com.fsoft.service;

import com.fsoft.dto.ColumnDto;
import com.fsoft.model.BoardColumn;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

public interface ColumnService {
    public ColumnDto addNewColumn(String title,
                                  String description,
                                  Date createdAt,
                                  UUID boardId);

    public void deleteColumn(String id,
                             UUID userId);

    public BoardColumn updateColumnDetails(String id,
                                           UUID boardId,
                                           UUID userId,
                                           String title,
                                           String description,
                                           Date createdAt);
}
