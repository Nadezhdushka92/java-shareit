package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.AddCommentRequestDto;
import ru.practicum.shareit.item.dto.AddItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid AddItemRequestDto newItemDto) {
        return itemClient.addNewItem(userId, newItemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return itemClient.findItemById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.findAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "") String text) {
        return itemClient.searchItem(userId, text);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody @Valid UpdateItemRequestDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("id") Long itemId,
                                             @RequestBody @Valid AddCommentRequestDto commentDto) {
        return itemClient.addNewComment(userId, itemId, commentDto);
    }
}