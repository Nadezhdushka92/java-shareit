package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Поиск пользователей: ");
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new EmptyException("Email пустой");
        }
        log.debug("Добавление пользователя : {}", user);
        return  toUserDto(repository.save(toUser(user)));
    }

    @Override
    public UserDto getUserById(long id) {
        checkUserById(id);
        log.debug("Поиск пользователя с Id: {}", id);
        return toUserDto(repository.getById(id));
    }

    @Override
    public UserDto update(UserDto userDto) {
        checkUserById(userDto.getId());
        log.debug("Обновление пользователя: {}", userDto);
        return toUserDto(repository
                .update(toUserUpdate(userDto, repository.getById(userDto.getId()))));
    }

    @Override
    public void delete(long id) {
        checkUserById(id);
        log.debug("Deleting user by id: {}", id);
        repository.delete(id);
    }

    private void checkUserById(Long userId) {
        if (repository.getById(userId) == null) {
            throw new ValidationException("Отсутствует пользователь с id: " + userId);
        }
    }
}