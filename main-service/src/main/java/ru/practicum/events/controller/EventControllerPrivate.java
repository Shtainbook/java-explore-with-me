package ru.practicum.events.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.model.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.model.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class EventControllerPrivate {

    private final EventService eventService;

    @Autowired
    public EventControllerPrivate(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getAllEventsByUserId(@PathVariable Long userId,
                                                                    @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                    @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос GET '/users/{}/events' для метода getAllEventsByUserId по userId={}", userId, userId);
        return new ResponseEntity<>(eventService.getAllEventsByUserId(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Запрос POST '/users/{}/events' для метода createEvent с newEventDto={} и userId={}", userId, newEventDto, userId);
        return new ResponseEntity<>(eventService.createEvent(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Запрос GET '/users/{}/events/{}' для метода getEventById с eventId={}", userId, eventId, eventId);
        return new ResponseEntity<>(eventService.getEventById(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequest) {
        log.info("Запрос PATCH '/users/{}/events/{}' для метода updateEventByUser с updateEventUserRequest={}",
                userId, eventId, updateEventUserRequest);
        return new ResponseEntity<>(eventService.updateEventByUser(userId, eventId, updateEventUserRequest), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@Valid @Positive @PathVariable Long userId,
                                                                     @Valid @Positive @PathVariable Long eventId) {
        log.info("Запрос GET '/users/{}/events/{}/requests' для метода getRequests с eventId={}", userId, eventId, eventId);
        return new ResponseEntity<>(eventService.getRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@Valid @Positive @PathVariable Long userId,
                                                                              @Valid @Positive @PathVariable Long eventId,
                                                                              @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Запрос PATCH '/users/{}/events/{}/requests' для метода updateRequestStatus с eventRequestStatusUpdateRequest={}", userId, eventId, eventRequestStatusUpdateRequest);
        return new ResponseEntity<>(eventService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest), HttpStatus.OK);
    }
}