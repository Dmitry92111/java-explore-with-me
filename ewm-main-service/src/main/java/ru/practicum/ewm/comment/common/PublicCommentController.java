package ru.practicum.ewm.comment.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;

import java.util.List;


@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    @GetMapping
    public List<CommentDto> getEventComments(@PathVariable long eventId,
                                             @Min(0) @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        return publicCommentService.getEventComments(eventId, from, size);
    }
}
