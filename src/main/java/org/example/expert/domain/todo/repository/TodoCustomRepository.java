package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.entity.dto.TodoSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {

    Optional<Todo> findByIdWithUser(final Long todoId);

    Page<TodoSearchResponse> findByFilter(
            final String title,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String nickname,
            final Pageable pageable
    );
}
