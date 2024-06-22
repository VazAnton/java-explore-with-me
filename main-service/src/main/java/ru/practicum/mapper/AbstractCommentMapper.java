package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.comment.CommentDtoInput;
import ru.practicum.dto.comment.CommentDtoOutput;
import ru.practicum.model.Comment;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AbstractEventMapper.class})
public abstract class AbstractCommentMapper {

    public Comment commentDtoInputToComment(CommentDtoInput commentDtoInput) {
        if (commentDtoInput == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setText(commentDtoInput.getText());
        return comment;
    }

    public abstract CommentDtoOutput commentToCommentDtoOutput(Comment comment);
}
