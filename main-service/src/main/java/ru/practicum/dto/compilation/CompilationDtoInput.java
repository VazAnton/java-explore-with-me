package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDtoInput {

    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
}
