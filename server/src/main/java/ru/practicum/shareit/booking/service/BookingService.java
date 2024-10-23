package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long bookerId, AddBookingDto newBookingDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String status);

    List<BookingDto> getBookingsByItemsOwner(Long ownerId, String state);
}