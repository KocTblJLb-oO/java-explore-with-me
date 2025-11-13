package ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.StatsRequest;
import ru.practicum.ewm.dto.ViewStats;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StatsClient {
    private final RestClient restClient;

    public StatsClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.restClient = RestClient.builder().baseUrl(statsServerUrl).build();
    }

    public void sendHit(EndpointHitDto endPointHitDto) {
        restClient.post()
                .uri("/hit")
                .body(endPointHitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(List<StatsRequest> statsRequests) {
        List<ViewStats> allStats = new ArrayList<>();
        for (StatsRequest statsRequest : statsRequests) {
            try {
                List<ViewStats> stats = restClient.get()
                        .uri(uriBuilder -> uriBuilder.path("/stats")
                                .queryParam("start", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        .format(statsRequest.getStart()))
                                .queryParam("end", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        .format(statsRequest.getEnd()))
                                .queryParam("uris", statsRequest.getUris())
                                .queryParam("unique", statsRequest.isUnique())
                                .build())
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });
                assert stats != null;
                allStats.addAll(stats);
            } catch (HttpStatusCodeException e) {
                log.error("Ошибка получения статистики: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            } catch (Exception e) {
                log.error("Ошибка при запросе статистики: {}", e.getMessage(), e);
            }
        }
        return allStats;
    }
}
