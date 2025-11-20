package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(String commentText, List<Long> users, List<Long> events, List<Long> comments,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        int from, int size) {

        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации", HttpStatus.BAD_REQUEST);
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdAt").descending());

        boolean filterText = commentText != null && !commentText.isBlank() || Objects.equals(commentText, "0");
        boolean filterUsers = users != null && !users.isEmpty() && !(users.size() == 1 && users.getFirst() == 0);
        boolean filterEvents = events != null && !events.isEmpty() && !(events.size() == 1 && events.getFirst() == 0);
        boolean filterComments = comments != null && !comments.isEmpty() &&
                !(comments.size() == 1 && comments.getFirst() == 0);
        boolean filterDates = rangeStart != null && rangeEnd != null && rangeStart.isBefore(rangeEnd);

        List<Comment> commentList = commentRepository.findWithFilters(
                filterText ? commentText : null,
                filterUsers ? users : null,
                filterEvents ? events : null,
                filterComments ? comments : null,
                filterDates ? rangeStart : null,
                filterDates ? rangeEnd : null,
                pageable
        );
        return commentMapper.toCommentDto(commentList);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getUserComment(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не найден",
                    HttpStatus.NOT_FOUND);
        }

        return commentMapper.toCommentDto(commentRepository.findAllByAuthorId(userId));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));

        comment.setCommentText(newCommentDto.getCommentText());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ValidationException("Комментарий с id " + commentId + " не найден",
                        HttpStatus.NOT_FOUND));

        commentRepository.deleteById(commentId);
    }
}