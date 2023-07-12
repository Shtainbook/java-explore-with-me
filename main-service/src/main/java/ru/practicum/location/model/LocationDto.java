package ru.practicum.location.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class LocationDto {
    private Float lat;
    private Float lon;
}