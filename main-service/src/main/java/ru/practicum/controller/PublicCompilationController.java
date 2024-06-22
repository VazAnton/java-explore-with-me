package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDtoOutput;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDtoOutput getCompilationById(@PathVariable long compId) {
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDtoOutput> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }
}
