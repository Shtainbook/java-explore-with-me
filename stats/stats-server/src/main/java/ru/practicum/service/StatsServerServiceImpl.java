package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.mapper.EndPointHitMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.repository.StatsServerRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@Transactional(readOnly = true)
public class StatsServerServiceImpl implements StatsServerService {

    private final StatsServerRepository statsServerRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsServerServiceImpl(StatsServerRepository statsServerRepository) {
        this.statsServerRepository = statsServerRepository;
    }

    @Override
    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit element = statsServerRepository.save(EndPointHitMapper.toEndpointHit(endpointHitDto));
        log.info("saveHit с данными EndpointHitDto сохранен: " + endpointHitDto.getId());
        return EndPointHitMapper.toEndpointHitDto(element);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(String start, String end, List<String> uri, Boolean unique) {

        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            startTime = LocalDateTime.parse(start, FORMATTER);
            endTime = LocalDateTime.parse(end, FORMATTER);
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Проблема с неверно переданным временем! Сверь часы!");
        }

        if (startTime.isAfter(endTime)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Старт не может быть после конца!");
        }

        if (unique) {
            if (uri != null && !uri.isEmpty()) {
                return statsServerRepository.getViewStatsWithUriAndUnique(startTime, endTime, uri);
            } else {
                return statsServerRepository.getViewStatsUnique(startTime, endTime);
            }
        } else {
            if (uri != null && !uri.isEmpty()) {
                return statsServerRepository.getViewStatsWithUris(startTime, endTime, uri);
            } else {
                return statsServerRepository.getViewStats(startTime, endTime);
            }
        }
    }
}