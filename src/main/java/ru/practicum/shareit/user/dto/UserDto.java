package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Builder
@Data
public class UserDto {

    private Long id;
    private String name;
    @Email(message = "Некорректный email")
    private String email;
}