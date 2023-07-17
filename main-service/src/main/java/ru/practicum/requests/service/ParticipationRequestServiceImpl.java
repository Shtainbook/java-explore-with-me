package ru.practicum.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.EventService;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestDto;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.enums.EventState.PUBLISHED;
import static ru.practicum.enums.RequestStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public ParticipationRequestServiceImpl(ParticipationRequestRepository participationRequestRepository, UserService userService, EventService eventService) {
        this.participationRequestRepository = participationRequestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUserModelById(userId);
        Event event = eventService.getEventModelById(eventId);

        checkBeforeCreate(event, userId);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(CONFIRMED);
        } else {
            participationRequest.setStatus(PENDING);
        }
        log.info("Вызов метода createRequest с userId={} с eventId={}", userId, eventId);
        return ParticipationRequestMapper.toRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userService.userExists(userId);

        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request", requestId));

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request", requestId);
        }

        participationRequest.setStatus(CANCELED);
        log.info("Вызов метода cancelRequest с requestId={}", requestId);
        return ParticipationRequestMapper.toRequestDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsByUserId(Long userId) {
        userService.userExists(userId);
        List<ParticipationRequest> requests = participationRequestRepository.findAllByRequesterId(userId);
        log.info("Вызов метода getAllRequestsByUserId с userId={}", userId);
        return ParticipationRequestMapper.toRequestDto(requests);
    }

    private void checkBeforeCreate(Event event, Long userId) {
        if (participationRequestRepository.findByRequesterIdAndEventId(userId, event.getId()) != null) {
            throw new ConflictException("Нельзя добавить повторный запрос.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может подавать заявку на участие в событии.");
        }

        if (event.getState() != PUBLISHED) {
            throw new ConflictException("Нельзя участвовать в неопубликовать в неопубликованном событии.");
        }

        Long confirmedCount = participationRequestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников достигнут.");
        }
    }
}