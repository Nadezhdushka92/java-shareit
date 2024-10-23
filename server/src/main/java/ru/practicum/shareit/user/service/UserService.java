package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    UserDto updateUserById(Long userId, UpdateUserRequestDto userDto);

    List<UserDto> findAll();

    UserDto findById(Long id);

    void deleteById(Long id);
}