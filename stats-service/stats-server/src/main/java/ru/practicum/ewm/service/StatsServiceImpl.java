package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.repository.StatsRepository;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            throw new ValidationException("Дата начала не может быть ранее даты окончания");
        }
        if (unique) {
            return statsRepository.getUniqueStats(start, end, uris);
        } else {
            return statsRepository.getStats(start, end, uris);
        }
    }
}
