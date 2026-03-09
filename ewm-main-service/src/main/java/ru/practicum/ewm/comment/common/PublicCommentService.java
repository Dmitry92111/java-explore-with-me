package ru.practicum.ewm.comment.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(long eventId, int from, int size) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId))
        );

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId));
        }

        Pageable pageable = PageRequest.of((from / size), size, Sort.by("createdOn").descending());

        List<Comment> comments = commentRepository.findByEvent_IdAndDeletedOnIsNull(eventId, pageable).getContent();

        return commentMapper.toDtos(comments);
    }
}
