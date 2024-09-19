package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUserId(Long userId);
    List<ItemDto> getItemsBySearch(String text);
    ItemDto getItemById(Long itemId, Long userId);
    ItemDto saveItem(Long userId, ItemDto item);
    ItemDto update(Long userId, ItemDto item);
    void deleteItem(Long userId, Long itemId);
}
