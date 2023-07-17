package ru.practicum.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location getByLatAndLong(Float lat, Float lon) {
        log.info("Вызов метода getByLatAndLong с lat={} and lon={}", lat, lon);
        return locationRepository.findFirstByLatAndLon(lat, lon);
    }

    @Override
    @Transactional
    public Location create(Location location) {
        log.info("Вызов метода create с location={}", location);
        return locationRepository.save(location);
    }

    @Override
    public Boolean locationExists(Float lat, Float lon) {
        log.info("Вызов метода locationExists с lat={} и lon={}", lat, lon);
        return locationRepository.findFirstByLatAndLon(lat, lon) != null;
    }

    @Override
    public Location getLocation(Float lat, Float lon) {

        if (locationExists(lat, lon)) {
            return getByLatAndLong(lat, lon);
        }
        Location location = Location.builder()
                .lat(lat)
                .lon(lon)
                .build();
        log.info("Вызов метода getLocation с lat={} и lon={}", lat, lon);
        return locationRepository.save(location);
    }
}