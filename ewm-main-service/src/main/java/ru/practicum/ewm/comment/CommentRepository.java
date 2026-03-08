package ru.practicum.ewm.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    Optional<Comment> findByIdAndEvent_IdAndAuthor_IdAndDeletedOnIsNull(Long id, Long eventId, Long authorId);

    @Modifying
    @Query("UPDATE Comment c " +
            "SET c.deletedOn = :deletedOn " +
            "WHERE c.id = :id " +
                "AND c.event.id = :eventId " +
                "AND c.author.id = :authorId " +
                "AND c.deletedOn IS NULL")
    int softDelete(@Param("id") long id,
                   @Param("eventId") long eventId,
                   @Param("authorId") long authorId,
                   @Param("deletedOn") LocalDateTime deletedOn);

    @Modifying
    @Query("UPDATE Comment c SET c.deletedOn = :deletedOn WHERE c.id = :id AND c.deletedOn IS NULL")
    int softDeleteForAdmin(@Param("id") long id,
                           @Param("deletedOn") LocalDateTime deletedOn);

    @EntityGraph(attributePaths = "author")
    Page<Comment> findByEvent_IdAndDeletedOnIsNull(Long eventId, Pageable pageable);

    @NonNull
    @Override
    @EntityGraph(attributePaths = "author")
    Page<Comment> findAll(@Nullable Specification<Comment> specification,
                          @NonNull Pageable pageable);
}
