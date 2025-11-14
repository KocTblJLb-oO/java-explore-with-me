package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

@Component
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent())
                .status(participationRequest.getStatus())
                .build();
    }

    ParticipationRequest toParticipationRequest(ParticipationRequestDto participationRequestDto) {
        return ParticipationRequest.builder()
                .requester(participationRequestDto.getRequester())
                .created(participationRequestDto.getCreated())
                .event(participationRequestDto.getEvent())
                .status(participationRequestDto.getStatus())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDtoList(List<ParticipationRequest> participationRequestList) {
        return participationRequestList.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();
    }
}
