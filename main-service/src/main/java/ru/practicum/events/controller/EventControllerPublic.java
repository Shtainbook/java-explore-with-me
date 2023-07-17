package ru.practicum.events.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.EventSortOptions;
import ru.practicum.events.model.EventFullDto;
import ru.practicum.events.model.EventShortDto;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventControllerPublic {

    private final EventService eventService;

    @Autowired
    public EventControllerPublic(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEventsByParams(@RequestParam(required = false) String text,
                                                                    @RequestParam(required = false) List<Long> categories,
                                                                    @RequestParam(required = false) Boolean paid,
                                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                    @RequestParam(required = false) EventSortOptions sort,
                                                                    @RequestParam(defaultValue = "0") Integer from,
                                                                    @RequestParam(defaultValue = "10") Integer size,
                                                                    HttpServletRequest request) {
        log.info("Запрос GET '/events' для метода getEventsByParams с параметрами: text={}, categories={}, " +
                        "paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return new ResponseEntity<>(eventService.getEventsByParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId,
                                                     HttpServletRequest request) {
        log.info("Запрос GET '/events/{}' для метода getEventById с eventId={}", eventId, eventId);
        return new ResponseEntity<>(eventService.getEventById(eventId, request), HttpStatus.OK);
    }
}