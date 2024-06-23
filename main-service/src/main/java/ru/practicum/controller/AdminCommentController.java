package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDtoOutput;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments/admin")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/users/{userId}")
    public List<CommentDtoOutput> getCommentsOfUser(@PathVariable long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsOfUser(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDtoOutput> getCommentsOfEvent(@PathVariable long eventId,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsOfEvent(eventId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentLikeAdmin(@PathVariable long commentId) {
        commentService.deleteCommentLikeAdmin(commentId);
    }
}
