package ru.practicum.location.mapper;

import ru.practicum.location.model.Location;
import ru.practicum.location.model.LocationDto;

public class LocationMapper {

    private LocationMapper() {
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}