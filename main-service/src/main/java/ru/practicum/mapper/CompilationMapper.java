package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.model.Compilation;
import ru.practicum.dto.compilation.CompilationDtoOutput;
import ru.practicum.dto.compilation.CompilationDtoInput;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

@Component
public class CompilationMapper {

    public CompilationDtoOutput compilationToCompilationDtoOutput(Compilation compilation) {
        if (compilation == null) {
            return null;
        }
        CompilationDtoOutput compilationDtoOutput = new CompilationDtoOutput();
        compilationDtoOutput.setId(compilation.getId());
        compilationDtoOutput.setPinned(compilation.getPinned());
        compilationDtoOutput.setTitle(compilation.getTitle());
        return compilationDtoOutput;
    }

    public Compilation compilationDtoInputToCompilation(CompilationDtoInput compilationDtoInput) {
        if (compilationDtoInput == null) {
            return null;
        }
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationDtoInput.getPinned());
        compilation.setTitle(compilationDtoInput.getTitle());
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
