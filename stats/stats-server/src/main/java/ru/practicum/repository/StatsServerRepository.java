package ru.practicum.repository;

import ru.practicum.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(element.app, element.uri, COUNT(DISTINCT element.ip)) " +
            "FROM EndpointHit element " +
            "WHERE element.uri in :uris AND timestamp BETWEEN :start AND :end " +
            "GROUP BY element.app, element.uri " +
            "ORDER BY COUNT(element.ip) DESC")
    List<ViewStatsDto> getViewStatsWithUriAndUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(element.app, element.uri, COUNT(DISTINCT element.ip)) " +
            "FROM EndpointHit element " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY element.app, element.uri " +
            "ORDER BY COUNT(element.ip) DESC")
    List<ViewStatsDto> getViewStatsUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(element.app, element.uri, COUNT(element.ip)) " +
            "FROM EndpointHit element " +
            "WHERE element.uri in :uris AND timestamp BETWEEN :start AND :end " +
            "GROUP BY element.app, element.uri " +
            "ORDER BY COUNT(element.ip) DESC")
    List<ViewStatsDto> getViewStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(element.app, element.uri, COUNT(element.ip)) " +
            "FROM EndpointHit element " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY element.app, element.uri " +
            "ORDER BY COUNT(element.ip) DESC")
    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end);
}