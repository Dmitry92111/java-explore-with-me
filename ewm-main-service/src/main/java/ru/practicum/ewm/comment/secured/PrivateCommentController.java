package ru.practicum.ewm.comment.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable long userId,
                             @PathVariable long eventId,
                             @Valid @RequestBody NewCommentDto dto) {
        return privateCommentService.create(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long eventId,
                             @PathVariable long commentId,
                             @Valid @RequestBody UpdateCommentDto dto) {
        return privateCommentService.update(dto, userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId,
                       @PathVariable long eventId,
                       @PathVariable long commentId) {
        privateCommentService.delete(userId, eventId, commentId);
    }
}
