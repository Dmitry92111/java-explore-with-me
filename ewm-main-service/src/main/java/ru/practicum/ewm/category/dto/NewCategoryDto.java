package ru.practicum.ewm.category.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.json_utils.TrimStringDeserializer;

@NoArgsConstructor
@Getter
@Setter
public class NewCategoryDto {

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
