package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}