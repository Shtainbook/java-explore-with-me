package ru.practicum.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.location.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull(message = "Поле annotation для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля annotation max длина = 2000 символов, а min длина = 20 символ", max = 2000, min = 20)
    private String annotation;
    @NotNull(message = "Поле category для которого был осуществлен запрос не может быть пустым или null")
    private Long category;
    @NotNull(message = "Поле description для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля description max длина = 7000 символов, а min длина = 20 символ", max = 7000, min = 20)
    private String description;
    @NotNull(message = "Поле eventDate для которого был осуществлен запрос не может быть пустым или null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    @NotNull(message = "Поле location для которого был осуществлен запрос не может быть пустым или null")
    private Location location;
    private Boolean paid = false;
    @PositiveOrZero(message = "Поле participantLimit не может быть меньше 0")
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotNull(message = "Поле title для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля title max длина = 120 символов, а min длина = 3 символ", max = 120, min = 3)
    private String title;
}