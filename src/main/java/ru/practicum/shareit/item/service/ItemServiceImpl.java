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

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        checkUserById(userId);
        log.info("Получение списка всех Items юзера с id:{}", userId);
        return itemRepository.findItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
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
    public ItemDto getItemById(long userId, long itemId) {
        checkUserById(userId);
        checkItemById(itemId);
        log.info("Поиск вещи c id:{} владелец с id: {}", itemId, userId);
        return toItemDto(itemRepository.findItemById(itemId));
    }

    @Override
    public ItemDto saveItem(long userId, ItemDto itemDto) {
        log.info("Добавление item : {}; Владелец: {}", itemDto, userId);
        checkUserById(userId);
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyException("Null в ItemDto");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyException("Empty в ItemDto");
        }
        return toItemDto(itemRepository.save(userId, toItem(itemDto)));
    }

    @Override
    public ItemDto update(long userId, ItemDto item) {
        checkUserById(userId);
        checkItemById(item.getId());
        log.info("Обновление item : {}; для пользователя {}", item, userId);
        return toItemDto(itemRepository.update(userId, toItemUpdate(item, itemRepository
                .findItemById(item.getId()))));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private void checkUserById(long userId) {
        if (userId == -1) {
            throw new ValidationException("Отсутствует пользователь с header-Id : " + userId);
        }
        if (userService.getAllUsers().stream().map(UserDto::getId).noneMatch(u -> u.equals(userId))) {
            throw new NotFoundException("Отсутствует пользователь с Id : " + userId);
        }
    }

    private void checkItemById(long itemId) {
        if (itemRepository.findItemById(itemId) == null) {
            throw new NotFoundException("Отсутствует вещь с id: " + itemId);
        }
    }
}