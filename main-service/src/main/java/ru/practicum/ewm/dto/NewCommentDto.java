package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewCommentDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 1000)
    private String commentText;
    private Long eventId;
    private Long authorId;
}