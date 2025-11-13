package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StatsRequest {
    private Set<String> uris;

    @Builder.Default
    private LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);

    @Builder.Default
    private LocalDateTime end = LocalDateTime.now();

    private boolean unique;
}
