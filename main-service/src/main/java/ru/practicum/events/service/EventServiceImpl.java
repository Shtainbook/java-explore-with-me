package ru.practicum.events.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.enums.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.*;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.location.LocationService;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestDto;
import ru.practicum.requests.repository.ParticipationRequestRepository;
import ru.practicum.users.service.UserService;
import ru.practicum.util.pageable.OffsetPageRequest;
import ru.practicum.util.validation.SizeValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, LocationService locationService, UserService userService, CategoryService categoryService, ParticipationRequestRepository participationRequestRepository, StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.participationRequestRepository = participationRequestRepository;
        this.statsClient = statsClient;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        Event event = EventMapper.toEvent(newEventDto);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Дата события должна быть на два часа раньше текущего времени.");
        }

        Category category = categoryService.getCategoryModelById(newEventDto.getCategory());

        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(userService.getUserModelById(userId));
        event.setState(EventState.PENDING);
        event.setLocation(locationService.getLocation(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()));

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(0L);
        eventFullDto.setConfirmedRequests(0L);
        log.info("Вызов метода createEvent с newEventDto={} и userId={}", newEventDto, userId);
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequest) {
        userService.userExists(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event", eventId));

        if (event.getState() != null && event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Могут быть изменены события только со статусами: PENDING и CANCELED.");
        }

        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Дата события должна быть на два часа раньше текущего времени.");
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryModelById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(locationService.getLocation(updateEventUserRequest.getLocation().getLat(),
                    updateEventUserRequest.getLocation().getLon()));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == EventStateActionUser.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            } else if (updateEventUserRequest.getStateAction() == EventStateActionUser.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }
        log.info("Вызов метода updateEventByUser с eventId={} with updateEventUserRequest={}", eventId, updateEventUserRequest);
        return toEventFullDtoWithViewsAndConfirmedRequests(EventMapper.toEventFullDto(event), event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequest) {
        Event event = getEventModelById(eventId);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationRequestException("Дата начала редактируемого события должна быть не ранее одного часа с момента публикации.");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == EventStateActionAdmin.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Невозможно опубликовать мероприятие, так как оно находится в неправильном состоянии: " + event.getState());
                }

                event.setPublishedOn(LocalDateTime.now());
                event.setState(EventState.PUBLISHED);
            } else if (updateEventAdminRequest.getStateAction() == EventStateActionAdmin.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Мероприятие может быть отклонено только если оно не опубликовано.");
                } else {
                    event.setState(EventState.CANCELED);
                }
            }
        }

        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationRequestException("Дата события должна быть на два часа раньше текущего времени..");
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryModelById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationService.getLocation(updateEventAdminRequest.getLocation().getLat(),
                    updateEventAdminRequest.getLocation().getLon()));
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        log.info("Вызов метода updateEventByAdmin с eventId={} with updateEventAdminRequest={}", eventId, updateEventAdminRequest);
        return toEventFullDtoWithViewsAndConfirmedRequests(EventMapper.toEventFullDto(eventRepository.save(event)), event);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.userExists(userId);
        Event event = getEventModelById(eventId);

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = EventRequestStatusUpdateResult.builder().build();

        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedList = new ArrayList<>();

        if (requests.isEmpty()) {
            return eventRequestStatusUpdateResult;
        }

        if (!requests.stream()
                .map(ParticipationRequest::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Могут быть изменены только запросы в статусе ожидание.");
        }

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new ConflictException("Запрос не найден.");
        }

        int limitParticipants = event.getParticipantLimit();

        if (limitParticipants == 0 || !event.getRequestModeration()) {
            return eventRequestStatusUpdateResult;
        }

        Long countParticipants = participationRequestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        if (countParticipants >= limitParticipants) {
            throw new ConflictException("Достигнут лимит участников.");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            for (ParticipationRequest element : requests) {
                element.setStatus(RequestStatus.REJECTED);
                rejectedList.add(ParticipationRequestMapper.toRequestDto(element));
            }
            eventRequestStatusUpdateResult.setRejectedRequests(rejectedList);
        } else {
            for (ParticipationRequest element : requests) {
                if (countParticipants < limitParticipants) {
                    element.setStatus(RequestStatus.CONFIRMED);
                    confirmedList.add(ParticipationRequestMapper.toRequestDto(element));
                    countParticipants++;
                } else {
                    element.setStatus(RequestStatus.REJECTED);
                    rejectedList.add(ParticipationRequestMapper.toRequestDto(element));
                }
            }
            eventRequestStatusUpdateResult.setConfirmedRequests(confirmedList);
            eventRequestStatusUpdateResult.setRejectedRequests(rejectedList);
        }
        participationRequestRepository.saveAll(requests);
        log.info("Вызов метода updateRequestStatus с eventId={} with eventRequestStatusUpdateRequest={}", eventId, eventRequestStatusUpdateRequest);
        return eventRequestStatusUpdateResult;
    }

    @Override
    public Event getEventModelById(Long id) {
        log.info("Вызов метода getEventModelById с id={}", id);
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event", id));
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        SizeValidator.validateSize(size);
        userService.userExists(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, OffsetPageRequest.of(from, size));
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViews(events);

        List<EventShortDto> eventShortDtoList = EventMapper.toEventShortDto(events);

        for (EventShortDto element : eventShortDtoList) {
            element.setViews(views.getOrDefault(element.getId(), 0L));
            element.setConfirmedRequests(confirmedRequests.getOrDefault(element.getId(), 0L));
        }
        log.info("Вызов метода getAllEventsByUserId c userId={}", userId);
        return eventShortDtoList;
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        userService.userExists(userId);
        Event event = getEventModelById(eventId);
        log.info("Вызов метода getEventById с eventId={}  userId={}", eventId, userId);
        return toEventFullDtoWithViewsAndConfirmedRequests(EventMapper.toEventFullDto(event), event);
    }

    @Override
    public List<EventFullDto> getEventsByUserIds(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {
        checkStartIsBeforeEnd(rangeStart, rangeEnd);
        checkStates(states);

        SizeValidator.validateSize(size);
        Pageable pageable = OffsetPageRequest.of(from, size);

        List<Event> events = eventRepository.findEventsByAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                pageable);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViews(events);

        List<EventFullDto> eventFullDtoList = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());


        for (EventFullDto element : eventFullDtoList) {
            element.setViews(views.getOrDefault(element.getId(), 0L));
            element.setConfirmedRequests(confirmedRequests.getOrDefault(element.getId(), 0L));
        }
        log.info("Вызов метода getEventsByUserIds с users={}", users);
        return eventFullDtoList;
    }

    @Override
    public List<EventShortDto> getEventsByParams(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSortOptions sort,
            Integer from,
            Integer size,
            HttpServletRequest request) {
        statsClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());

        SizeValidator.validateSize(size);
        Pageable pageable = OffsetPageRequest.of(from, size);
        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<Event> events = eventRepository.findPublishedEventsByUser(
                text,
                categories,
                paid,
                rangeStart != null ? rangeStart : LocalDateTime.now(),
                rangeEnd,
                pageable);

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> views = getViews(events);

        List<EventShortDto> eventShortDtoList = EventMapper.toEventShortDto(events);

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        for (EventShortDto element : eventShortDtoList) {
            element.setViews(views.getOrDefault(element.getId(), 0L));
            element.setConfirmedRequests(confirmedRequests.getOrDefault(element.getId(), 0L));
        }

        if (onlyAvailable) {
            eventShortDtoList = eventShortDtoList.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                    break;
                case VIEWS:
                    eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
                    break;
                default:
                    throw new ValidationRequestException("У метода getEventsByParams проблема с валидацией");
            }
        }
        log.info("Вызов метода getEventsByParams с text={}", text);
        return eventShortDtoList;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userService.userExists(userId);

        List<Event> events = eventRepository.findByIdAndInitiatorId(eventId, userId);
        List<ParticipationRequest> requests = participationRequestRepository.findByEventIn(events);
        log.info("Вызов метода getRequests с userId={} и eventId={}", userId, eventId);
        return ParticipationRequestMapper.toRequestDto(requests);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {

        statsClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr());
        Event event = getEventModelById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event", eventId);
        }
        log.info("Вызов метода getEventById с eventId={}", eventId);
        return toEventFullDtoWithViewsAndConfirmedRequests(EventMapper.toEventFullDto(event), event);
    }

    private EventFullDto toEventFullDtoWithViewsAndConfirmedRequests(EventFullDto eventFullDto, Event event) {
        Map<Long, Long> confirmedRequests = getConfirmedRequests(List.of(event));
        Map<Long, Long> views = getViews(List.of(event));

        eventFullDto.setViews(views.getOrDefault(eventFullDto.getId(), 0L));
        eventFullDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventFullDto.getId(), 0L));
        return eventFullDto;
    }

    private Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        if (events.isEmpty()) {
            return views;
        }

        List<Event> publishedEvents = getPublishedEvents(events);

        Optional<LocalDateTime> minDate = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minDate.isPresent()) {
            LocalDateTime start = minDate.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(Event::getId)
                    .map(id -> ("/events/" + id))
                    .collect(Collectors.toList());

            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri()
                        .split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }

        return views;
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        return participationRequestRepository.findAllByEventInAndStatus(getPublishedEvents(events), RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.groupingBy(participationRequest ->
                        participationRequest.getEvent().getId(), Collectors.counting()));
    }

    private List<Event> getPublishedEvents(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }

    private void checkStartIsBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationRequestException("Дата начала позже даты конца.");
        }
    }

    private void checkStates(List<String> states) {
        if (states != null) {
            for (String element : states) {
                try {
                    EventState.valueOf(element);
                } catch (IllegalArgumentException e) {
                    throw new ValidationRequestException("Неверное значение в методе checkStates.");
                }
            }
        }
    }
}