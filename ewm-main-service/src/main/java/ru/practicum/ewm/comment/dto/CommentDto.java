package ru.practicum.ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentDto {
    private final long id;
    private final String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedOn;

    private final UserShortDto author;
}
