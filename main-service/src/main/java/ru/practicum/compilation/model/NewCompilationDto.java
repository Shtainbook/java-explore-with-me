package ru.practicum.compilation.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private Boolean pinned;
    @NotBlank(message = "Поле title для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля title max длина = 50 символов, а min длина = 1 символ", max = 50, min = 1)
    private String title;
}