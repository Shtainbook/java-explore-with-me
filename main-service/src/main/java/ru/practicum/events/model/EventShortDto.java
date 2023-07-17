package ru.practicum.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.users.model.UserShortDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    @NotNull(message = "Поле annotation для которого был осуществлен запрос не может быть пустым или null")
    private String annotation;
    @NotNull(message = "Поле category для которого был осуществлен запрос не может быть пустым или null")
    private CategoryDto category;
    @PositiveOrZero(message = "Поле confirmedRequests не может быть меньше 0")
    private Long confirmedRequests;
    @NotNull(message = "Поле eventDate для которого был осуществлен запрос не может быть пустым или null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Long id;
    @NotNull(message = "Поле initiator для которого был осуществлен запрос не может быть пустым или null")
    private UserShortDto initiator;
    @NotNull(message = "Поле paid для которого был осуществлен запрос не может быть пустым или null")
    private Boolean paid;
    @NotNull(message = "Поле title для которого был осуществлен запрос не может быть пустым или null")
    private String title;
    private Long views;
}