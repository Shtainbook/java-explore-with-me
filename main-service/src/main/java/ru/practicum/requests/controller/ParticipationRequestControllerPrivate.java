package ru.practicum.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.model.ParticipationRequestDto;
import ru.practicum.requests.service.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class ParticipationRequestControllerPrivate {

    private final ParticipationRequestService requestService;

    @Autowired
    public ParticipationRequestControllerPrivate(ParticipationRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAll(@PathVariable Long userId) {
        log.info("Запрос GET '/users/{userId}/requests' для метода getAllRequestsByUserId с userId={}", userId);
        return new ResponseEntity<>(requestService.getAllRequestsByUserId(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable Long userId,
                                                          @Valid @Positive @RequestParam Long eventId) {
        log.info("Запрос POST '/users/{}/requests' для метода createRequest с userId={} на событие с eventId={}", userId, userId, eventId);
        return new ResponseEntity<>(requestService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable Long userId,
                                                          @PathVariable Long requestId) {
        log.info("Запрос PATCH '/users/{}/requests/{}/cancel' для метода cancelRequest с requestId={} от пользователя с userId={}", userId, requestId, requestId, userId);
        return new ResponseEntity<>(requestService.cancelRequest(userId, requestId), HttpStatus.OK);
    }
}