package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findItemsByUserId(long userId);

    Item findItemById(long itemId);

    Item save(long userId, Item item);

    Item update(Long userId, Item item);

    List<Item> getItemsBySearch(String text);

    void deleteByUserIdAndItemId(long userId, long itemId);
}