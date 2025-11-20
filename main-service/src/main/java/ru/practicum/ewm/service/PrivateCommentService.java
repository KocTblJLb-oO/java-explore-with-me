package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден",
                        HttpStatus.NOT_FOUND));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id " + eventId + " не найдено",
                        HttpStatus.NOT_FOUND));


        Comment comment = Comment.builder()
                .commentText(newCommentDto.getCommentText())
                .event(event)
                .author(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto getComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));

        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Можно редактировать только свои комментарии",
                    HttpStatus.FORBIDDEN);
        }

        comment.setCommentText(newCommentDto.getCommentText());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }


    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Можно редактировать только свои комментарии",
                    HttpStatus.FORBIDDEN);
        }

        commentRepository.deleteById(commentId);
    }
}