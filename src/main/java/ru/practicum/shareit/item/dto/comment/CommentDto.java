package ru.practicum.shareit.item.dto.comment;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;
    private String authorName;
    private ItemDto item;
    private String text;
    private LocalDateTime created;
}