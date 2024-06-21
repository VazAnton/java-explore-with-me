package ru.practicum.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryDtoOutput {

    private Long id;
    @NotNull
    @Length(min = 1, max = 50)
    private String name;
}
