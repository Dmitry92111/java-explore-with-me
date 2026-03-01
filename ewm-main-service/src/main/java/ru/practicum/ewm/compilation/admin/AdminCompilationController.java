package ru.practicum.ewm.compilation.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationService;

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        return adminCompilationService.create(dto);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        adminCompilationService.delete(compId);
    }

    @PatchMapping("{compId}")
    public CompilationDto update(@PathVariable long compId,
                                 @RequestBody UpdateCompilationRequest dto) {
        return adminCompilationService.update(compId, dto);
    }
}
