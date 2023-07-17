package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsServerService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class StatsController {

    private final StatsServerService statsService;

    @Autowired
    public StatsController(StatsServerService  statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Вызван метод saveHit для ендпонита Post с данными EndpointHitDto: " + endpointHitDto);
        return new ResponseEntity<>(statsService.saveHit(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    private ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam String start,
                                                        @RequestParam String end,
                                                        @RequestParam(required = false) List<String> uris,
                                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Вызван метод getStats для ендпонита Get '/stats'  с данными start={}, end={}, uri={}, unique={}", start, end, uris, unique);
        return new ResponseEntity<>(statsService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}