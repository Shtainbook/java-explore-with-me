package ru.practicum.requests.mapper;

import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {
    private ParticipationRequestMapper() {
    }

    public static ParticipationRequestDto toRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .id(participationRequest.getId())
                .requester(participationRequest.getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toRequestDto(Iterable<ParticipationRequest> eventRequests) {
        List<ParticipationRequestDto> result = new ArrayList<>();
        for (ParticipationRequest element : eventRequests) {
            result.add(toRequestDto(element));
        }
        return result;
    }
}