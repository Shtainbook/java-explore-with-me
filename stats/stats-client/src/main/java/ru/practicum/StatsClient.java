package ru.practicum;

import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {

    void saveHit(String app, String uri, String ip);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}