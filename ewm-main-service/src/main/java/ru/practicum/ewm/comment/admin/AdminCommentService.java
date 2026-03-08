package ru.practicum.ewm.comment.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.Comment;
import ru.practicum.ewm.comment.CommentMapper;
import ru.practicum.ewm.comment.CommentRepository;
import ru.practicum.ewm.comment.CommentSpecification;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.error.exception.BadRequestException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.event.util.EventUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentService {
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> findComments(List<Long> users,
                                         String text,
                                         String rangeStart,
                                         String rangeEnd,
                                         int from,
                                         int size) {

        LocalDateTime start = EventUtils.parseDateOrNull(rangeStart, "rangeStart");
        LocalDateTime end = EventUtils.parseDateOrNull(rangeEnd, "rangeEnd");

        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException(ExceptionMessages.START_DATE_AFTER_END_DATE);
        }

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                Sort.by(Sort.Direction.DESC, "createdOn")
        );

        Specification<Comment> specification = Specification
                .where(CommentSpecification.notDeleted())
                .and(CommentSpecification.authorIdIn(users))
                .and(CommentSpecification.textContains(text))
                .and(CommentSpecification.createdOnAfterOrEqual(start))
                .and(CommentSpecification.createdOnBeforeOrEqual(end));

        List<Comment> comments = commentRepository.findAll(specification, pageable).getContent();

        return commentMapper.toDtos(comments);
    }

    @Transactional
    public void delete(long commentId) {
        int updated = commentRepository.softDeleteForAdmin(
                commentId,
                LocalDateTime.now()
        );

        if (updated == 0) {
            throw new NotFoundException(String.format(ExceptionMessages.COMMENT_NOT_FOUND, commentId));
        }
    }
}
