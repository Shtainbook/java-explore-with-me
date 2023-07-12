package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public EndpointHitDto(Long id, String app, String uri, String ip, LocalDateTime timestamp) {
        this.id = id;
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}