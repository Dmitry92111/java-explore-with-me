package ru.practicum.ewm.comment.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.json_utils.TrimStringDeserializer;


@Getter
@Setter
@NoArgsConstructor
public class UpdateCommentDto {
    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(max = 2000)
    private String text;
}
