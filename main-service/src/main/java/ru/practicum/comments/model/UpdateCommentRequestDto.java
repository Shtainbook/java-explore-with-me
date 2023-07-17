package ru.practicum.comments.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequestDto {
    @NotBlank(message = "Поле id для которого был осуществлен запрос не может быть пустым")
    @Size(message = "У поля name max длина = 1000 символов, а min длина = 3 символ", max = 1000, min = 3)
    private String commentText;
}