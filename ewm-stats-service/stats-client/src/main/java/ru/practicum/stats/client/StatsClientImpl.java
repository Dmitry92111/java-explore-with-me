package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.client.exception.StatsClientException;
import ru.practicum.stats.dto.EndpointHitCreateDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StatsClientImpl(@Value("${stats.server.url}") String baseUrl,
                           RestTemplate statsRestTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = statsRestTemplate;
    }

    @Override
    public void hit(EndpointHitCreateDto dto) {
        try {
            restTemplate.postForEntity(baseUrl + "/hit", dto, Void.class);
        } catch (HttpStatusCodeException ex) {
            throw new StatsClientException("Stats service error on POST /hit. Status=" + ex.getStatusCode()
                    + ", body=" + safeBody(ex), ex);
        } catch (ResourceAccessException ex) {
            throw new StatsClientException("Stats service is unreachable on POST /hit: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        String url = buildUrlForStatsRequest(start, end, uris, unique);

        try {
            ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(url, ViewStatsDto[].class);
            ViewStatsDto[] body = response.getBody();
            return body == null ? List.of() : List.of(body);
        } catch (HttpStatusCodeException ex) {
            throw new StatsClientException("Stats service error on GET /stats. Status=" + ex.getStatusCode()
                    + ", body=" + safeBody(ex), ex);
        } catch (ResourceAccessException ex) {
            throw new StatsClientException("Stats service is unreachable on GET /stats: " + ex.getMessage(), ex);
        }
    }

    private String buildUrlForStatsRequest(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", format(start))
                .queryParam("end", format(end));

        if (unique) {
            builder.queryParam("unique", true);
        }

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }

        return builder.build().encode().toUriString();
    }

    private String format(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static String safeBody(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        return body.isBlank() ? "<empty>" : body;
    }
}
