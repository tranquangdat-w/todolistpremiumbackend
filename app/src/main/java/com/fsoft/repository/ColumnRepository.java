package com.fsoft.repository;

import com.fsoft.model.Columnn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<Columnn, String> {
}
