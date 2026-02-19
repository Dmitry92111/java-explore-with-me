package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EndpointHitCreateDto(

        @NotBlank
        @Size(max = 64)
        String app,

        @NotBlank
        @Size(max = 128)
        String uri,

        @NotBlank
        @Size(max = 45)
        String ip,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
}
