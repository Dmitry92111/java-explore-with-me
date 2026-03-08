package ru.practicum.ewm.comment.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentDto> findComments(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @Min(0) @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        return adminCommentService.findComments(users, text, rangeStart, rangeEnd, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) {
        adminCommentService.delete(commentId);
    }
}
