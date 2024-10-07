package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> getItemsBySearch(String text);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto saveItem(Long userId, ItemDto item);

    ItemDto update(Long userId, ItemDto item);

    void deleteItem(Long itemId);

    ItemDto checkItemOwner(Long ownerId, Long itemId);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
