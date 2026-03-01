package ru.practicum.ewm.event.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> findEvents(@RequestParam String text,
                                          @RequestParam List<Long> categories,
                                          @RequestParam Boolean paid,
                                          @RequestParam String rangeStart,
                                          @RequestParam String rangeEnd,
                                          @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                          @RequestParam String sort,
                                          @Min(0) @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size,
                                          HttpServletRequest request) {
        publicEventService.saveHit(request);

        return publicEventService.findEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size);
    }

    @GetMapping("/{id}")
    public EventFullDto findEventById(@PathVariable(name = "id") long eventId,
                                      HttpServletRequest request) {
        publicEventService.saveHit(request);
        return publicEventService.findEventById(eventId);
    }
}
