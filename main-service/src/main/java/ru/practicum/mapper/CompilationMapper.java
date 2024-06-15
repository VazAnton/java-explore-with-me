package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.dto.compilation.CompilationDto;
import ru.practicum.model.dto.compilation.NewCompilationDto;
import ru.practicum.model.dto.compilation.UpdateCompilationRequest;

@Component
public class CompilationMapper {

    public CompilationDto compilationToCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        return compilationDto;
    }

    public Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public Compilation updateCompilationRequestToCompilation(UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest == null) {
            return null;
        }
        Compilation compilation = new Compilation();
        compilation.setPinned(updateCompilationRequest.getPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());
        return compilation;
    }
}
