package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsServerServiceImpl;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Slf4j
public class StatsServerController {

    private final StatsServerServiceImpl statsServerService;

    @Autowired
    public StatsServerController(StatsServerServiceImpl statsServerService) {
        this.statsServerService = statsServerService;
    }

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Вызван метод saveHit для ендпонита Post с данными EndpointHitDto: " + endpointHitDto);
        return new ResponseEntity<>(statsServerService.saveHit(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam(name = "start") String start,
                                                       @RequestParam(name = "end") String end,
                                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Вызван метод getStats для ендпонита Get '/stats'  с данными start={}, end={}, uri={}, unique={}", start, end, uris, unique);
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        return new ResponseEntity<>(statsServerService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}