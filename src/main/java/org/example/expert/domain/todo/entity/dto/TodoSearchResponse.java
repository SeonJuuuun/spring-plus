package org.example.expert.domain.todo.entity.dto;

public record TodoSearchResponse(
        String title,
        Long managerCount,
        Long commentCount
) {
}
