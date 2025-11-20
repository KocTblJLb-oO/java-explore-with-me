package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
    @NotNull(message = "id не должен быть пустым")
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}
