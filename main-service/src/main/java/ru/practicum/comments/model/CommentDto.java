package ru.practicum.comments.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @NotNull(message = "Поле id для которого был осуществлен запрос не может быть пустым или null")
    private Long id;
    @NotNull(message = "Поле authorName для которого был осуществлен запрос не может быть пустым или null")
    private String authorName;
    @NotNull(message = "Поле commentText для которого был осуществлен запрос не может быть пустым или null")
    private String commentText;
    @NotNull(message = "Поле createdOn для которого был осуществлен запрос не может быть пустым или null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private String createdOn;
    private Integer likesCount;
    private Integer dislikesCount;
}
