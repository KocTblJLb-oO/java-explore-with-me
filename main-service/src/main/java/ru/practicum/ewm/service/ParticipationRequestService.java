package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService {
    private final UserService userService;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventRepository eventRepository;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        if (!userService.userExists(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не найден", HttpStatus.NOT_FOUND);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id " + eventId + " не найдено",
                        HttpStatus.NOT_FOUND));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Нельзя подать заявку на участие в неопубликованном событии",
                    HttpStatus.CONFLICT);
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор не может подать заявку на участие в своём событии",
                    HttpStatus.CONFLICT);
        }

        if (participationRequestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ValidationException("Запрос " + eventId + "на участие уже существует", HttpStatus.CONFLICT);
        }

        long confirmedRequests = participationRequestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ValidationException("Достигнуто максимальное количество участников для события c id: " + eventId,
                    HttpStatus.CONFLICT);
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event.getId())
                .requester(userId)
                .status(Boolean.TRUE.equals(event.getRequestModeration()) ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest savedRequest = participationRequestRepository.save(request);

        return ParticipationRequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id " + eventId + " не найдено",
                        HttpStatus.NOT_FOUND));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор может управлять заявками на участие", HttpStatus.FORBIDDEN);
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(request.getRequestIds());
        if (requests.isEmpty()) {
            throw new ValidationException("Заявки не найдены", HttpStatus.NOT_FOUND);
        }

        for (ParticipationRequest participationRequest : requests) {
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Изменять статус можно только у заявок в ожидании", HttpStatus.CONFLICT);
            }
        }

        long confirmedRequests = participationRequestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ValidationException("Достигнут лимит заявок на участие", HttpStatus.CONFLICT);
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest participationRequest : requests) {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                if (confirmedRequests < event.getParticipantLimit() || event.getParticipantLimit() == 0) {
                    participationRequest.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(participationRequest);
                    confirmedRequests++;
                } else {
                    participationRequest.setStatus(RequestStatus.REJECTED);
                    rejected.add(participationRequest);
                }
            } else {
                participationRequest.setStatus(RequestStatus.REJECTED);
                rejected.add(participationRequest);
            }
        }

        participationRequestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                participationRequestMapper.toParticipationRequestDtoList(confirmed),
                participationRequestMapper.toParticipationRequestDtoList(rejected)
        );
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userService.userExists(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не найден.", HttpStatus.NOT_FOUND);
        }
        List<ParticipationRequest> requests = participationRequestRepository.findByRequester(userId);
        return requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        if (!userService.userExists(userId)) {
            throw new ValidationException("Пользователь с id=" + userId + " не найден.", HttpStatus.NOT_FOUND);
        }

        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ValidationException("Запрос на участие с id=" + requestId + " не найден.", HttpStatus.NOT_FOUND));

        participationRequest.setStatus(RequestStatus.CANCELED);

        participationRequestRepository.save(participationRequest);

        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id=" + eventId + " не найдено.", HttpStatus.NOT_FOUND));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор события может просматривать заявки на участие.", HttpStatus.FORBIDDEN);
        }

        List<ParticipationRequest> requests = participationRequestRepository.findByEvent(eventId);
        return participationRequestMapper.toParticipationRequestDtoList(requests);
    }
}
