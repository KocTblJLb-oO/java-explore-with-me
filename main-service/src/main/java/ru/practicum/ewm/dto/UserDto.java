package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    String name;

    @Email(message = "Email должен иметь формат адреса электронной почты")
    @NotBlank(message = "Email не должен быть пустым")
    String email;
}
