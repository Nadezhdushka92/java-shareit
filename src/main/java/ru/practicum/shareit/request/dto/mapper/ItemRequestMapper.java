package ru.practicum.shareit.request.dto.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestMapper {
public static ItemRequestDto itemRequestDto(ItemRequest itemRequest) {
    return ItemRequestDto.builder()
            .requester(itemRequest.getRequester())
            .created(itemRequest.getCreated())
            .description(itemRequest.getDescription())
            .build();
}
}