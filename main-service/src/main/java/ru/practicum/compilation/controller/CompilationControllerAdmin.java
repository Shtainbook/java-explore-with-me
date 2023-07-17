package ru.practicum.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.NewCompilationDto;
import ru.practicum.compilation.model.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @Autowired
    public CompilationControllerAdmin(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("запрос POST '/admin/compilations' для метода createCompilation с newCompilationDto={}", newCompilationDto);
        return new ResponseEntity<>(compilationService.createCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("запрос PATCH '/admin/compilations/{}' для метода updateCompilation для compId={} с updateCompilationRequest={}",
                compId, compId, updateCompilationRequest);
        return new ResponseEntity<>(compilationService.updateCompilation(compId, updateCompilationRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<?> deleteCompilation(@PathVariable Long compId) {
        log.info("запрос DELETE '/admin/compilations/{}' для удаления подброки с compId={}", compId, compId);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}