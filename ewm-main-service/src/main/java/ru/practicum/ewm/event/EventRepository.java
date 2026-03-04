package ru.practicum.ewm.event;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.entity.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value =
            "SELECT e.id " +
            "FROM events e " +
            "WHERE e.initiator_id = :initiatorId " +
            "ORDER BY e.id " +
            "LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Long> findEventIdsByInitiatorId(@Param("initiatorId") long initiatorId,
                                         @Param("from") int from,
                                         @Param("size") int size);

    @Query("SELECT DISTINCT e " +
            "FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "WHERE e.id IN :ids")
    List<Event> findAllWithCategoryAndInitiatorByIdIn(@Param("ids") Collection<Long> ids);

    Optional<Event> findByIdAndInitiator_Id(long eventId, long initiatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e from Event e where e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") long id);

    boolean existsByCategory_Id(long categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id = :eventId AND e.initiator.id = :userId")
    Optional<Event> findByIdAndInitiatorIdForUpdate(@Param("eventId") long eventId,
                                                    @Param("userId") long userId);

    @Query("SELECT e " +
            "FROM Event e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "WHERE e.id = :eventId")
    Optional<Event> findByIdWithInitiatorAndCategory(@Param("eventId") long eventId);
}
