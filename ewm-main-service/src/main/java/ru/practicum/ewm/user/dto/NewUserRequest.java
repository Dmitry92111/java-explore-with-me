package ru.practicum.ewm.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.json_utils.TrimStringDeserializer;

@Getter
@Setter
@NoArgsConstructor
public class NewUserRequest {

    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    private String email;


    @JsonDeserialize(using = TrimStringDeserializer.class)
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}
