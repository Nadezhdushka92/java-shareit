package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addNewUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserById(Long userId, UpdateUserRequestDto updateUserDto) {
        UserDto userDto = UserMapper.mapToUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Не найден пользователь id = " + userId)));
        if (updateUserDto.getName() != null) {
            userDto.setName(updateUserDto.getName());
        }
        if (updateUserDto.getEmail() != null) {
            userDto.setEmail(updateUserDto.getEmail());
        }
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " +
                                                         id)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}