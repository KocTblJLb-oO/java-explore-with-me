package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String commentText;
    private EventShortDto event;
    private UserShortDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}