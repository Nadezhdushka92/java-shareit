package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class BookingIdDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private Long bookerId;
    private BookingStatus status;
    private Long itemId;
}