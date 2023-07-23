package ru.practicum.comments.mapper;

import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentDto;
import ru.practicum.comments.model.NewCommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    private CommentMapper() {
    }

    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .commentText(newCommentDto.getCommentText())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .commentText(comment.getCommentText())
                .createdOn(comment.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }

    public static List<CommentDto> toCommentDto(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment element : comments) {
            result.add(toCommentDto(element));
        }
        return result;
    }
}