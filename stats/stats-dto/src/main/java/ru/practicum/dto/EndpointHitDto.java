package ru.practicum.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class EndpointHitDto {
    private Long id;
    @NotBlank(message = "Идентификатор сервиса для которого записывается информация app не может быть пустым или null")
    private String app;
    @NotBlank(message = "URI для которого был осуществлен запрос не может быть пустым или null")
    private String uri;
    @NotBlank(message = "IP-адрес пользователя, осуществившего запрос не может быть пустым или null")
    private String ip;
    @NotNull(message = "Дата и время, когда был совершен запрос к эндпоинту не может быть пустым или null")
    private String timestamp;
}