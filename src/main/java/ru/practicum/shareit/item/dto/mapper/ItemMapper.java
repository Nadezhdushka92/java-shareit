package ru.practicum.shareit.item.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : 0)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }

    public static Item toItemUpdate(ItemDto itemDto, Item item) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName() != null ? itemDto.getName() : item.getName())
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription())
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable())
                .request(item.getRequest())
                .owner(item.getOwner())
                .build();
    }
    }

