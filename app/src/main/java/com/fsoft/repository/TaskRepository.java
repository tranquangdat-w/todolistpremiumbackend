package com.fsoft.repository;

import com.fsoft.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, String> {
    public Task findById(UUID id);

    public ArrayList<Task> findByColumnId(UUID id);
}
