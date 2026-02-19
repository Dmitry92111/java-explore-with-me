package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
                    SELECT new ru.practicum.stats.dto.ViewStatsDto(
                        e.app,
                        e.uri,
                        COUNT(e)
                    )
                    FROM EndpointHit e
                    WHERE e.created BETWEEN :start AND :end
                    GROUP BY e.app, e.uri
                    ORDER BY COUNT(e) DESC
            """)
    List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end);

    @Query("""
                    SELECT new ru.practicum.stats.dto.ViewStatsDto(
                        e.app,
                        e.uri,
                        COUNT(DISTINCT e.ip)
                    )
                    FROM EndpointHit e
                    WHERE e.created BETWEEN :start AND :end
                    GROUP BY e.app, e.uri
                    ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStatsDto> findStatsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("""
                    SELECT new ru.practicum.stats.dto.ViewStatsDto(
                        e.app,
                        e.uri,
                        COUNT(e)
                    )
                    FROM EndpointHit e
                    WHERE e.created BETWEEN :start AND :end
                    AND (e.uri IN :uris)
                    GROUP BY e.app, e.uri
                    ORDER BY COUNT(e) DESC
            """)
    List<ViewStatsDto> findStatsWhereUrisIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
                    SELECT new ru.practicum.stats.dto.ViewStatsDto(
                        e.app,
                        e.uri,
                        COUNT(DISTINCT e.ip)
                    )
                    FROM EndpointHit e
                    WHERE e.created BETWEEN :start AND :end
                    AND (e.uri IN :uris)
                    GROUP BY e.app, e.uri
                    ORDER BY COUNT(DISTINCT e.ip) DESC
            """)
    List<ViewStatsDto> findStatsWhereUrisInWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
