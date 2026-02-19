package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EndpointHitCreateDto {

        @NotBlank
        @Size(max = 64)
        private final String app;

        @NotBlank
        @Size(max = 128)
        private final String uri;

        @NotBlank
        @Size(max = 45)
        private final String ip;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private final LocalDateTime timestamp;

        public EndpointHitCreateDto(String app,
                                    String uri,
                                    String ip,
                                    LocalDateTime timestamp) {
                this.app = app;
                this.uri = uri;
                this.ip = ip;
                this.timestamp = timestamp;
        }

        public String getApp() {
                return app;
        }

        public String getUri() {
                return uri;
        }

        public String getIp() {
                return ip;
        }

        public LocalDateTime getTimestamp() {
                return timestamp;
        }
}
