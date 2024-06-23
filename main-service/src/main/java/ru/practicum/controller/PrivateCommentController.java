package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDtoInput;
import ru.practicum.dto.comment.CommentDtoOutput;
import ru.practicum.service.comment.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoOutput addCommentToEvent(@RequestBody @Valid CommentDtoInput commentDtoInput,
                                              @PathVariable long eventId,
                                              @RequestHeader("X-Explorer-User-Id") long userId) {
        return commentService.addCommentToEvent(commentDtoInput, eventId, userId);
    }

    @PatchMapping("/{commentId}/events/{eventId}")
    public CommentDtoOutput patchComment(@RequestBody @Valid CommentDtoInput commentDtoInput,
                                         @PathVariable long commentId,
                                         @PathVariable long eventId,
                                         @RequestHeader("X-Explorer-User-Id") long userId) {
        return commentService.patchComment(commentDtoInput, commentId, eventId, userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentLikeUser(@PathVariable long commentId,
                               @RequestHeader("X-Explorer-User-Id") long userId) {
        commentService.deleteCommentLikeUser(commentId, userId);
    }
}
