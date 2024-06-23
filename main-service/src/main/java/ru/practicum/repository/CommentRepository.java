package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM comments " +
                    "WHERE user_id=? " +
                    "LIMIT ? " +
                    "OFFSET ?;")
    List<Comment> getCommentsByAuthor(long userId, Integer size, Integer from);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM comments " +
                    "WHERE event_id=? " +
                    "LIMIT ? " +
                    "OFFSET ?;")
    List<Comment> getAllByEventId(long eventId, Integer size, Integer from);
}
