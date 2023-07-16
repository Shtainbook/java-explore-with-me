package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsServerRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsServerService {

    private final StatsServerRepository statsServerRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsServiceImpl(StatsServerRepository statsServerRepository) {
        this.statsServerRepository = statsServerRepository;
    }

    @Override
    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit element = statsServerRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        log.info("saveHit с данными EndpointHitDto сохранен: " + endpointHitDto.getId());
        return EndpointHitMapper.toEndpointHitDto(element);
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), FORMATTER);

        if (startTime.isAfter(endTime)) {
            throw new ValidationRequestException("Старт не может быть после конца.");
        }

        if (unique) {
            return (uris != null) ? statsServerRepository.getViewStatsWithUriAndUnique(startTime, endTime, uris)
                    : statsServerRepository.getViewStatsUnique(startTime, endTime);
        } else {
            return (uris != null) ? statsServerRepository.getViewStatsWithUris(startTime, endTime, uris)
                    : statsServerRepository.getViewStats(startTime, endTime);
        }
    }
}