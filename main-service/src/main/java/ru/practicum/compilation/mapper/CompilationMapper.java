package ru.practicum.compilation.mapper;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.events.mapper.EventMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompilationMapper {

    private CompilationMapper() {
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents() != null
                        ? EventMapper.toEventShortDtoSet(compilation.getEvents())
                        : Set.of())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned() != null
                        ? newCompilationDto.getPinned()
                        : false)
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static List<CompilationDto> toCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation element : compilations) {
            result.add(toCompilationDto(element));
        }
        return result;
    }
}