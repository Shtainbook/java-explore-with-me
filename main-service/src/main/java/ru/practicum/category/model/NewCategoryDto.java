package ru.practicum.category.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Поле name для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля name max длина = 50 символов, а min длина = 1 символ", max = 50, min = 1)
    private String name;
}