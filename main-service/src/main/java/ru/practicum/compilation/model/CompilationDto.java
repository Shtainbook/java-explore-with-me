package ru.practicum.compilation.model;

import lombok.*;
import ru.practicum.events.model.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    @NotNull(message = "Поле pinned для которого был осуществлен запрос не может быть пустым или null")
    private Boolean pinned;
    @NotNull(message = "Поле title для которого был осуществлен запрос не может быть пустым или null")
    private String title;
}