package ru.practicum.ewm.mapper;

import ru.practicum.ewm.model.EndpointHit;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.EndpointHitDto;

@Component
public class EndpointHitMapper {
    public EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(null,
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp()
        );
    }
}
