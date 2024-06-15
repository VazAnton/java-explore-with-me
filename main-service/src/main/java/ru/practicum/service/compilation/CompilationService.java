package ru.practicum.service.compilation;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.model.dto.compilation.CompilationDto;
import ru.practicum.model.dto.compilation.NewCompilationDto;
import ru.practicum.model.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto compilationDtoInput);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compId);

    void deleteCompilation(long compId);

    CompilationDto getCompilationById(long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
