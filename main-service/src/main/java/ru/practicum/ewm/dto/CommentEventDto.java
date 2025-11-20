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
public class CommentEventDto {
    private Long id;
    private String commentText;
    private UserShortDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}