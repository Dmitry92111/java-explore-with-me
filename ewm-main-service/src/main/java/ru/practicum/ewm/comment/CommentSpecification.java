package ru.practicum.ewm.comment;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public final class CommentSpecification {
    private CommentSpecification() {
    }

    public static Specification<Comment> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedOn"));
    }

    public static Specification<Comment> authorIdIn(List<Long> userIds) {
        return (root, query, cb) -> {
            if (userIds == null || userIds.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("author").get("id").in(userIds);
        };
    }

    public static Specification<Comment> textContains(String text) {
        return (root, query, cb) -> {
            if (text == null || text.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get("text")),
                    "%" + text.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Comment> createdOnAfterOrEqual(LocalDateTime start) {
        return (root, query, cb) -> {
            if (start == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("createdOn"), start);
        };
    }

    public static Specification<Comment> createdOnBeforeOrEqual(LocalDateTime end) {
        return (root, query, cb) -> {
            if (end == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("createdOn"), end);
        };
    }
}
