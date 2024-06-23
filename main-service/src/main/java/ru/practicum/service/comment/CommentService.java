package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDtoInput;
import ru.practicum.dto.comment.CommentDtoOutput;

import java.util.List;

public interface CommentService {

    CommentDtoOutput addCommentToEvent(CommentDtoInput commentDtoInput, long eventId, long userId);

    CommentDtoOutput patchComment(CommentDtoInput commentDtoInput, long commentId, long eventId, long userId);

    CommentDtoOutput getCommentById(long commentId);

    List<CommentDtoOutput> getCommentsOfUser(long userId, Integer from, Integer size);

    List<CommentDtoOutput> getCommentsOfEvent(long eventId, Integer from, Integer size);

    void deleteCommentLikeAdmin(long commentId);

    void deleteCommentLikeUser(long commentId, long userId);
}
