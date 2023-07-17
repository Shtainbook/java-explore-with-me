package ru.practicum.users.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Поле email для которого был осуществлен запрос не может быть пустым или null")
    @Email(message = "У поля email должна быть введеная электронная почта")
    @Size(message = "У поля email max длина = 254 символов, а min длина = 6 символ", max = 254, min = 6)
    private String email;
    @NotBlank(message = "Поле name для которого был осуществлен запрос не может быть пустым или null")
    @Size(message = "У поля name max длина = 250 символов, а min длина = 2 символ", max = 250, min = 2)
    private String name;
}