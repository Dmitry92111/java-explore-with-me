package ru.practicum.ewm.event.location;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
