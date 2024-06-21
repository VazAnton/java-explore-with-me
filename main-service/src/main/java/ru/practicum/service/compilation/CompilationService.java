package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDtoOutput;
import ru.practicum.dto.compilation.CompilationDtoInput;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDtoOutput addCompilation(CompilationDtoInput compilationDtoInput);

    CompilationDtoOutput updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compId);

    void deleteCompilation(long compId);

    CompilationDtoOutput getCompilationById(long compId);

    List<CompilationDtoOutput> getCompilations(Boolean pinned, Integer from, Integer size);
}
