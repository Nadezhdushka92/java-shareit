package ru.practicum.shareit.item;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, HttpServletRequest request) {
        return itemService.saveItem((long) request.getIntHeader("X-Sharer-User-Id"), itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, HttpServletRequest request) {
        itemDto.setId(itemId);
        return itemService.update((long) request.getIntHeader("X-Sharer-User-Id"), itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, HttpServletRequest request) {
        return itemService.getItemById((long) request.getIntHeader("X-Sharer-User-Id"), itemId);
    }

    @GetMapping()
    public List<ItemDto> getUserItems(HttpServletRequest request) {
        return itemService.getItemsByUserId((long) request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        return itemService.getItemsBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentToItem(@PathVariable Long itemId, @RequestBody CommentDto comment, HttpServletRequest request) {
        comment.setCreated(LocalDateTime.now());
        return itemService.addCommentToItem((long) request.getIntHeader("X-Sharer-User-Id"), itemId, comment);
    }
}
