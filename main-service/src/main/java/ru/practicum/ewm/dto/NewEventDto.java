package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @NotNull
    @Builder.Default
    private Boolean paid = false;

    @NotNull
    @PositiveOrZero
    @Builder.Default
    private Integer participantLimit = 0;

    @NotNull
    @Builder.Default
    private Boolean requestModeration = Boolean.TRUE;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
