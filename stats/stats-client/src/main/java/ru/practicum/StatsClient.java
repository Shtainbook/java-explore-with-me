package ru.practicum;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {

    EndpointHitDto saveHit(String app, String uri, String ip, LocalDateTime timestamp);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}