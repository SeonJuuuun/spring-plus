package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository{

    @Query("SELECT t FROM Todo t WHERE " +
            "( :weather IS NULL OR t.weather = :weather ) AND " +
            "( :startDate IS NULL OR t.modifiedAt >= :startDate ) AND " +
            "( :endDate IS NULL OR t.modifiedAt <= :endDate ) " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findByTodoByFilter(
            @Param("weather") final String weather,
            @Param("startDate") final LocalDateTime startDate,
            @Param("endDate") final LocalDateTime endDate,
            final Pageable pageable
    );
}
