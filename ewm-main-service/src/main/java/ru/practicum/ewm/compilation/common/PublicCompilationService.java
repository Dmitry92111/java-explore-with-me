package ru.practicum.ewm.compilation.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.assembler.EventShortDtoAssembler;
import ru.practicum.ewm.event.metrics.EventMetricsService;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCompilationService {
    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    private final EventMetricsService eventMetricsService;
    private final EventShortDtoAssembler eventShortDtoAssembler;

    @Transactional(readOnly = true)
    public List<CompilationDto> findCompilations(Boolean pinned,
                                                 int from,
                                                 int size) {

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        Page<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAllBy(pageable);
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        List<Compilation> compilationList = compilations.getContent();

        return compilationList.stream().map(
                compilation -> compilationMapper.toDto(compilation,
                        new HashSet<>(eventShortDtoAssembler.toEventShortDtos(compilation.getEvents().stream().toList()))
                )).toList();
    }

    @Transactional(readOnly = true)
    public CompilationDto findCompilationById(long compId) {
        Compilation compilation = compilationRepository.findWithGraphById(compId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.COMPILATION_NOT_FOUND, compId)));

        return compilationMapper.toDto(compilation, new HashSet<>(
                eventShortDtoAssembler.toEventShortDtos(
                        compilation.getEvents().stream().toList())));
    }
}
