package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.entity.dto.TodoSearchResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private static final QTodo TODO = QTodo.todo;
    private static final QUser USER = QUser.user;

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(final Long todoId) {
        final Todo result = queryFactory
                .selectFrom(TODO)
                .leftJoin(TODO.user, USER).fetchJoin()
                .where(TODO.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> findByFilter(
            final String title,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String nickname,
            final Pageable pageable
    ) {
        final QManager manager = QManager.manager;
        final QComment comment = QComment.comment;

        final List<TodoSearchResponse> resp = queryFactory
                .select(
                        Projections.constructor(
                                TodoSearchResponse.class,
                                TODO.title,
                                manager.countDistinct(),
                                comment.countDistinct()
                        ))
                .from(TODO)
                .leftJoin(TODO.managers, manager)
                .leftJoin(TODO.comments, comment)
                .leftJoin(manager.user, USER)
                .where(
                        titleContains(title),
                        createdBetween(startDate, endDate),
                        managerNicknameContains(nickname)
                )
                .groupBy(TODO.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(TODO.createdAt.desc())
                .fetch();

        final long total = queryFactory
                .select(TODO.countDistinct())
                .from(TODO)
                .leftJoin(TODO.managers, manager)
                .leftJoin(manager.user, USER)
                .leftJoin(TODO.comments, comment)
                .where(
                        titleContains(title),
                        createdBetween(startDate, endDate),
                        managerNicknameContains(nickname)
                )
                .fetchOne();

        return new PageImpl<>(resp, pageable, total);
    }

    private BooleanExpression titleContains(final String title) {
        return title != null ? TODO.title.contains(title) : null;
    }

    private BooleanExpression createdBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return TODO.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return TODO.createdAt.goe(startDate);
        } else if (endDate != null) {
            return TODO.createdAt.loe(endDate);
        }
        return null;
    }

    private BooleanExpression managerNicknameContains(final String nickname) {
        final QManager manager = QManager.manager;
        return nickname != null ? manager.user.nickname.contains(nickname) : null;
    }
}
