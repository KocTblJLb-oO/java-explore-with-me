package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit() != null ?
                        event.getParticipantLimit() : 0)
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration() != null ?
                        event.getRequestModeration() : true)
                .state(event.getState())
                .paid(event.isPaid())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .build();
    }

    public List<EventFullDto> toEventFullDtoList(List<Event> events) {
        return events.stream()
                .map(this::toEventFullDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public Set<EventShortDto> toEventShortDtoSet(Set<Event> events) {
        return events.stream()
                .map(this::toEventShortDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .state(EventState.PENDING)
                .category(newEventDto.getCategory() == null ? null : Category.builder()
                        .id(newEventDto.getCategory()).build())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .title(newEventDto.getTitle())
                .location(newEventDto.getLocation())
                .participantLimit(newEventDto.getParticipantLimit())
                .annotation(newEventDto.getAnnotation())
                .requestModeration(newEventDto.getRequestModeration())
                .description(newEventDto.getDescription())
                .build();
    }
}
