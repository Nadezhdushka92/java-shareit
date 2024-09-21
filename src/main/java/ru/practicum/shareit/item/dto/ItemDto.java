package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    //private Long owner;
    private Boolean available;
    //private Long requestId;
    private ItemRequest request;
}