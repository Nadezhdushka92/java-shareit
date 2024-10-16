package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DublicateException;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
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
        log.info("Поиск пользователей: ");
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new EmptyException("Email пустой");
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new DublicateException("Email дублируется");
        }
        log.info("Добавление пользователя : {}", user);
        return  toUserDto(repository.save(toUser(user)));
    }

    @Override
    public UserDto getUserById(long id) {
        checkUserById(id);
        log.info("Поиск пользователя с Id: {}", id);
        User userFromRep = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отсутствует пользователь с id: " + id));
        return toUserDto(userFromRep);
    }

    @Override
    public UserDto update(UserDto userDto) {
        checkUserById(userDto.getId());
        log.debug("Обновление пользователя: {}", userDto);

        if (repository.existsByEmail(userDto.getEmail())) {
            throw new DublicateException("Email дублируется");
        }
        User userToUpdate = toUserUpdate(userDto, repository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Отсутсвует пользователь с id: " + userDto.getId())));
        repository.save(userToUpdate);
        return toUserDto(userToUpdate);
    }

    @Override
    public void delete(long id) {
        User userFromDb = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отсутствует пользователь с id: " + id));
        log.info("Удаление пользователя с id : {}", id);
        repository.deleteById(userFromDb.getId());
    }

    private void checkUserById(long userId) {
        if (repository.getById(userId) == null) {
            throw new ValidationException("Отсутствует пользователь с id: " + userId);
        }
    }
}