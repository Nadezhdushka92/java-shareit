package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(BookingDto bookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long ownerId, String approve);

    BookingDto getBookingInfo(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByUserId(Long userId, BookingState state);

    List<BookingDto> getAllBookingsByOwnerId(Long ownerId, BookingState state);
}
