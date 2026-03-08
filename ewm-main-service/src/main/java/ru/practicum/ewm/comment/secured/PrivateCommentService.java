package ru.practicum.ewm.comment.secured;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.error.exception.ConditionsNotMetException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventStatus;
import ru.practicum.ewm.participation_request.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto create(NewCommentDto dto, long userId, long eventId) {
        User author = findUserOrThrow404(userId);

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId))
        );

        if (event.getState() != EventStatus.PUBLISHED) {
            throw new NotFoundException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId));
        }

        if (event.isRequestModeration()) {
            boolean isUserParticipant = participationRequestRepository.existsByEvent_IdAndRequester_IdAndStatus(
                    eventId,
                    userId,
                    ParticipationRequestStatus.CONFIRMED
            );
            if (!isUserParticipant) {
                throw new ConditionsNotMetException(
                        ExceptionMessages.USER_IS_NOT_ALLOWED_TO_COMMENT_EVENT_FOR_PARTICIPANTS_ONLY);
            }
        }

        Comment comment = commentMapper.fromNewCommentDto(dto);
        comment.setEvent(event);
        comment.setAuthor(author);

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Transactional
    public CommentDto update(UpdateCommentDto dto, long authorId, long eventId, long commentId) {
        findUserOrThrow404(authorId);

        Comment comment = commentRepository
                .findByIdAndEvent_IdAndAuthor_IdAndDeletedOnIsNull(commentId, eventId, authorId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.COMMENT_NOT_FOUND, commentId)));

        comment.setText(dto.getText());

        return commentMapper.toDto(comment);
    }

    @Transactional
    public void delete(long authorId, long eventId, long commentId) {
        findUserOrThrow404(authorId);

        int updated = commentRepository.softDelete(
                commentId,
                eventId,
                authorId,
                LocalDateTime.now()
        );

        if (updated == 0) {
            throw new NotFoundException(String.format(ExceptionMessages.COMMENT_NOT_FOUND, commentId));
        }
    }

    private User findUserOrThrow404(long userId) {
        return userRepository.findByIdAndDeletedIsFalse(userId).orElseThrow(() ->
                new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId))
        );
    }
}
