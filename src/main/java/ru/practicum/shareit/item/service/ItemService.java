package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsBySearch(String text);

    ItemDto getItemById(long userId, long itemId);

    ItemDto saveItem(long userId, ItemDto item);

    ItemDto update(long userId, ItemDto item);

    void deleteItem(long userId, long itemId);
}
