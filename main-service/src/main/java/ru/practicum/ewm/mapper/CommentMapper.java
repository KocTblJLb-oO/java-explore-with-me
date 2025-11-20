package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentEventDto;
import ru.practicum.ewm.model.Comment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .event(eventMapper.toEventShortDto(comment.getEvent()))
                .author(userMapper.toUserShortDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .commentText(commentDto.getCommentText())
                .event(eventMapper.toEvent(commentDto.getEvent()))
                .author(userMapper.toUser(commentDto.getAuthor()))
                .createdAt(commentDto.getCreatedAt())
                .updatedAt(commentDto.getUpdatedAt())
                .build();
    }

    public CommentEventDto toCommentEventDto(Comment comment) {
        return CommentEventDto.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .author(userMapper.toUserShortDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public List<CommentEventDto> toCommentEventDto(List<Comment> comments) {
        return comments.stream()
                .map(this::toCommentEventDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}