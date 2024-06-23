package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDtoInput;
import ru.practicum.dto.comment.CommentDtoOutput;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.AbstractCommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final AbstractCommentMapper commentMapper;


    private Comment getCommentFromDb(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! Комментария с таким уникальным номером не существует!"));
    }

    @Override
    public CommentDtoOutput addCommentToEvent(CommentDtoInput commentDtoInput, long eventId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!"));
        Comment comment = commentMapper.commentDtoInputToComment(commentDtoInput);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("Новый комментарий к событию успешно добавлен!");
        return commentMapper.commentToCommentDtoOutput(comment);
    }

    @Override
    public CommentDtoOutput patchComment(CommentDtoInput commentDtoInput, long commentId, long eventId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! Пользователя с таким уникальным номером не существует!"));
        eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Внимание! События с таким уникальным номером не существует!"));
        Comment comment = getCommentFromDb(commentId);
        if (comment.getEvent().getId() != eventId) {
            log.error("Выбранный комментарий не относится к данному событию!");
            throw new IncorrectDataException("Внимание! Выбранный комментарий не относится к данному событию!");
        }
        if (comment.getAuthor().getId() != userId) {
            log.error("Выбранный комментарий разместил другой пользователь!");
            throw new IncorrectDataException("Внимание! Выбранный комментарий разместил другой пользователь!");
        }
        comment.setText(commentDtoInput.getText());
        commentRepository.save(comment);
        log.info("Выбранный комментарий успешно изменён!");
        return commentMapper.commentToCommentDtoOutput(comment);
    }

    @Override
    public CommentDtoOutput getCommentById(long commentId) {
        Comment comment = getCommentFromDb(commentId);
        log.info("Успешно получена информация о выбранном комментарии!");
        return commentMapper.commentToCommentDtoOutput(comment);
    }

    @Override
    public List<CommentDtoOutput> getCommentsOfUser(long userId, Integer from, Integer size) {
        userService.getUser(userId);
        return commentRepository.getCommentsByAuthor(userId, size, from).stream()
                .map(commentMapper::commentToCommentDtoOutput)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDtoOutput> getCommentsOfEvent(long eventId, Integer from, Integer size) {
        return commentRepository.getAllByEventId(eventId, size, from)
                .stream()
                .map(commentMapper::commentToCommentDtoOutput)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentLikeAdmin(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Внимание! Комментария с таким уникальным номером не существует!");
        } else {
            commentRepository.deleteById(commentId);
            log.info("Выбранный комментарий успешно удалён!");
        }
    }

    @Override
    public void deleteCommentLikeUser(long commentId, long userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Внимание! Комментария с таким уникальным номером не существует!");
        } else {
            if (getCommentById(commentId).getAuthor().getId() != userId) {
                log.error("Только пользователь, оставивший комментарий к событию, может его удалить!");
                throw new IncorrectDataException("Внимание! Только пользователь, оставивший комментарий к событию, " +
                        "может его удалить!");
            }
            commentRepository.deleteById(commentId);
            log.info("Выбранный комментарий успешно удалён!");
        }
    }
}
