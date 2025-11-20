package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @Email(message = "Email должен иметь формат адреса электронной почты")
    @NotBlank(message = "Email не должен быть пустым")
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 250)
    private String name;
}
