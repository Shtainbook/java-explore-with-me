package ru.practicum.compilation.service;

import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    List<CompilationDto> getCompilationsByPinned(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long id);

    Compilation getCompilationModelById(Long id);

    CompilationDto updateCompilation(Long id, UpdateCompilationRequest updateCompilationRequest);

    void deleteCompilation(Long id);

    void compilationExists(Long id);
}