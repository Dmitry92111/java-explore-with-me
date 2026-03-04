package ru.practicum.ewm.participation_request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT r.event.id as eventId, COUNT(r.id) as count " +
            "FROM ParticipationRequest r " +
            "WHERE r.status = :status " +
            "AND r.event.id IN :eventIds " +
            "GROUP BY r.event.id")
    List<EventCountView> countByEventIdsAndStatus(@Param("eventIds") List<Long> eventIds,
                                                  @Param("status") ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByIdIn(Collection<Long> ids);

    long countByEvent_IdAndStatus(long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByRequester_Id(long requesterId);

    List<ParticipationRequest> findAllByEvent_Id(long eventId);

    List<ParticipationRequest> findAllByIdInAndEvent_Id(Collection<Long> ids, Long eventId);

    List<ParticipationRequest> findAllByEvent_IdAndStatus(long eventId, ParticipationRequestStatus status);

    Optional<ParticipationRequest> findByIdAndRequester_Id(long id, long requesterId);

    @Modifying
    @Query("UPDATE ParticipationRequest r " +
            "SET r.status = :newStatus " +
            "WHERE r.requester.id = :userId AND r.status = :oldStatus")
    int updateParticipationRequestsStatusByRequesterId(@Param("userId") long userId,
                                                       @Param("oldStatus") ParticipationRequestStatus oldStatus,
                                                       @Param("newStatus") ParticipationRequestStatus newStatus);
}
