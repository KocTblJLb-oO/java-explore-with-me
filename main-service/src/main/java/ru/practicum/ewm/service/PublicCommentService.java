package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id " + eventId + " не найдено",
                        HttpStatus.NOT_FOUND));

        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return commentMapper.toCommentDto(comments);
    }

    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));

        return commentMapper.toCommentDto(comment);
    }
}