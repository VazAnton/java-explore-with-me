package ru.practicum.model.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewCategoryDto {

    @NotNull
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}
