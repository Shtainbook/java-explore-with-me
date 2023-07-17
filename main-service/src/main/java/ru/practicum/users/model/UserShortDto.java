package ru.practicum.users.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {
    @NotNull(message = "Поле id для которого был осуществлен запрос не может быть пустым или null")
    private Long id;
    @NotNull(message = "Поле name для которого был осуществлен запрос не может быть пустым или null")
    private String name;
}