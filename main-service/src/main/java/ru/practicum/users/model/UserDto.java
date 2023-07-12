package ru.practicum.users.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull(message = "Поле email для которого был осуществлен запрос не может быть пустым или null")
    @Email(message = "У поля email должна быть введеная электронная почта")
    private String email;
    private Long id;
    @NotNull(message = "Поле name для которого был осуществлен запрос не может быть пустым или null")
    private String name;
}