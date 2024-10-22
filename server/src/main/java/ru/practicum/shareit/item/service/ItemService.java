package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AddItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRelatedDataDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.comment.AddCommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long ownerId, AddItemRequestDto item);

    ItemDto updateItemById(Long ownerId, Long itemId, UpdateItemRequestDto updateItemDto);

    ItemWithRelatedDataDto findById(Long userId, Long id);

    List<ItemWithRelatedDataDto> findAllWithRelatedDataByOwner(Long ownerId);

    List<ItemDto> search(Long ownerId, String text);

    CommentDto addNewComment(Long userId, Long itemId, AddCommentRequestDto commentDto);
}