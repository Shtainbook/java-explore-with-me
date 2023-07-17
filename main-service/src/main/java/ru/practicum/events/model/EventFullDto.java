package ru.practicum.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.enums.EventState;
import ru.practicum.location.model.LocationDto;
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
public class EventFullDto {
    private String annotation;
    @NotNull(message = "Поле category для которого был осуществлен запрос не может быть пустым или null")
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;
    private String description;
    @NotNull(message = "Поле eventDate для которого был осуществлен запрос не может быть пустым или null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Long id;
    @NotNull(message = "Поле initiator для которого был осуществлен запрос не может быть пустым или null")
    private UserShortDto initiator;
    @NotNull(message = "Поле location для которого был осуществлен запрос не может быть пустым или null")
    private LocationDto location;
    @NotNull(message = "Поле paid для которого был осуществлен запрос не может быть пустым или null")
    private Boolean paid;
    @PositiveOrZero(message = "Поле participantLimit не может быть меньше 0")
    private Integer participantLimit = 0;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration = true;
    private EventState state;
    @NotNull(message = "Поле title для которого был осуществлен запрос не может быть пустым или null")
    private String title;
    private Long views;
}