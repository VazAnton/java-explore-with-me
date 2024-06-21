package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDtoOutput;
import ru.practicum.dto.compilation.CompilationDtoInput;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDtoOutput addCompilation(@RequestBody @Valid CompilationDtoInput compilationDtoInput) {
        return compilationService.addCompilation(compilationDtoInput);
    }

    @PatchMapping("/{compId}")
    public CompilationDtoOutput updateCompilation(@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest,
                                                  @PathVariable long compId) {
        return compilationService.updateCompilation(updateCompilationRequest, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteCompilation(compId);
    }
}
