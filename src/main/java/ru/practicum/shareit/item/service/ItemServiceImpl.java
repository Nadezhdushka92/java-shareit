package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        checkUserById(userId);
        log.debug("Получение списка всех Items юзера с id:{}", userId);
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.debug("Поиск вещи : {} ", text);
        return itemRepository.getItemsBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        checkUserById(userId);
        checkItemById(itemId);
        log.debug("Поиск вещи c id:{} владелец с id: {}", itemId, userId);
        return toItemDto(itemRepository.findItemById(itemId));
    }

    @Override
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        log.info("Добавление item. Id юзера:{} , item:{}", userId, itemDto);
        checkUserById(userId);
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyException("Null в ItemDto");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyException("Empty в ItemDto");
        }
        log.debug("Создание item : {}; for user {}", itemDto, userId);
        return toItemDto(itemRepository.save(userId, toItem(itemDto)));
    }

    @Override
    public ItemDto update(Long userId, ItemDto item) {
        checkUserById(userId);
        checkItemById(item.getId());
        log.debug("Обновление item : {}; для пользователя {}", item, userId);
        return toItemDto(itemRepository.update(userId, toItemUpdate(item, itemRepository
                .findItemById(item.getId()))));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private void checkUserById(Long userId) {
        if (userId == -1) {
            throw new ValidationException("Отсутствует пользователь с header-Id : " + userId);
        }
        if (userService.getAllUsers().stream().map(UserDto::getId).noneMatch(u -> u.equals(userId))) {
            throw new NotFoundException("Отсутствует пользователь с Id : " + userId);
        }
    }

    private void checkItemById(Long itemId) {
        if (itemRepository.findItemById(itemId) == null) {
            throw new NotFoundException("Отсутствует вещь с id: " + itemId);
        }
    }
}