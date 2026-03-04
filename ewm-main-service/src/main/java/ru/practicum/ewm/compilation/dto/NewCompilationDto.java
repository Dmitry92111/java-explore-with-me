package ru.practicum.ewm.compilation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.json_utils.TrimStringDeserializer;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class NewCompilationDto {

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private boolean pinned;

    private Set<@Positive Long> events;
}
