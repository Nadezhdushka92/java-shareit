package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRelatedDataDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.comment.AddCommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long ownerId, AddItemDto item);

    ItemDto updateItemById(Long ownerId, Long itemId, UpdateItemDto updateItemDto);

    ItemWithRelatedDataDto findById(Long userId, Long id);

    List<ItemWithRelatedDataDto> findAllWithRelatedDataByOwner(Long ownerId);

    List<ItemDto> search(Long ownerId, String text);

    CommentDto addNewComment(Long userId, Long itemId, AddCommentDto commentDto);
}