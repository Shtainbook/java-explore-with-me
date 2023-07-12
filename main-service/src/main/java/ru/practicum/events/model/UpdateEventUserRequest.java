package ru.practicum.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.enums.EventStateActionUser;
import ru.practicum.location.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Size(message = "У поля annotation max длина = 2000 символов, а min длина = 20 символ", max = 2000, min = 20)
    private String annotation;
    private Long category;
    @Size(message = "У поля description max длина = 7000 символов, а min длина = 20 символ", max = 7000, min = 20)
    private String description;
    @Future(message = "У поля eventDate значение должно быть в будущем")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero(message = "Поле participantLimit не может быть меньше 0")
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateActionUser stateAction;
    @Size(message = "У поля title max длина = 120 символов, а min длина = 3 символ", max = 120, min = 3)
    private String title;
}