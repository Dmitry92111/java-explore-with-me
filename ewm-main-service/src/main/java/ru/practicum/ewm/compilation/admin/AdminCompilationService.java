package ru.practicum.ewm.compilation.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.metrics.EventMetricsService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMetricsService eventMetricsService;

    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.toEntity(dto);

        List<Event> events = loadEventsOrThrow(dto.getEvents());
        compilation.setEvents(new HashSet<>(events));

        Compilation saved = compilationRepository.save(compilation);
        return compilationMapper.toDto(saved, toEventShortDtos(events));
    }

    @Transactional
    public void delete(long compId) {
        if (compilationRepository.deleteByIdReturningCount(compId) == 0) {
            throw new NotFoundException(String.format(ExceptionMessages.COMPILATION_NOT_FOUND, compId));
        }
    }

    @Transactional
    public CompilationDto update(long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findByIdWithEvents(compId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ExceptionMessages.COMPILATION_NOT_FOUND,
                        compId
                )));

        if (dto.getTitle() != null) compilation.setTitle(dto.getTitle());
        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());

        List<Event> eventsForDto;
        if (dto.getEvents() != null) {
            List<Event> events = loadEventsOrThrow(dto.getEvents());
            compilation.setEvents(new HashSet<>(events));
            eventsForDto = events;
        } else {
            eventsForDto = new ArrayList<>(compilation.getEvents());
        }

        return compilationMapper.toDto(compilation, toEventShortDtos(eventsForDto));
    }

    private Set<EventShortDto> toEventShortDtos(List<Event> events) {
        if (events.isEmpty()) return Collections.emptySet();

        Map<Long, Long> confirmed = eventMetricsService.getConfirmedRequestsForEvents(events);
        Map<Long, Long> views = eventMetricsService.getViewsStatsForEvents(events);

        return events.stream()
                .map(e -> eventMapper.toEventShortDto(
                        e,
                        confirmed.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)
                ))
                .collect(Collectors.toSet());
    }

    private List<Event> loadEventsOrThrow(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();

        List<Event> events = eventRepository.findAllWithCategoryAndInitiatorByIdIn(ids);
        if (events.size() != ids.size()) {
            throw new NotFoundException(ExceptionMessages.SOME_EVENTS_IN_NEW_COMPILATION_DO_NOT_EXIST);
        }
        return events;
    }
}
