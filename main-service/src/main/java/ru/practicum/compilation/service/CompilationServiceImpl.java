package ru.practicum.compilation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.util.pageable.OffsetPageRequest;
import ru.practicum.util.validation.SizeValidator;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findEventByIdIn(newCompilationDto.getEvents()));
        }
        log.info("Вызов метода createCompilation с newCompilationDto={}", newCompilationDto);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilationsByPinned(Boolean pinned, Integer from, Integer size) {
        SizeValidator.validateSize(size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, OffsetPageRequest.of(from, size));
        } else {
            compilations = compilationRepository.findAll(OffsetPageRequest.of(from, size)).toList();
        }
        log.info("Вызов метода getCompilationsByPinned c параметрами: pinned={}, from={}, size={}", pinned, from, size);
        return CompilationMapper.toCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        log.info("Вызов метода getCompilationById с id={}", id);
        return CompilationMapper.toCompilationDto(getCompilationModelById(id));
    }

    @Override
    public Compilation getCompilationModelById(Long id) {
        log.info("Вызов метода getCompilationModelById с id={}", id);
        return compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Compilation", id));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationModelById(id);
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findEventByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        log.info("Вызов метода updateCompilation с updateCompilationRequest={}", updateCompilationRequest);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        compilationExists(id);
        log.info("Вызов метода deleteCompilation с id={}", id);
        compilationRepository.deleteById(id);
    }

    @Override
    public void compilationExists(Long id) {
        log.info("Вызов метода compilationExists с id={}", id);
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation", id);
        }
    }
}