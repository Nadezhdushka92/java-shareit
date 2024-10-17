package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto addNewBooking(Long bookerId, AddBookingRqDto newBookingDto) {
        Item item = itemRepository.findByIdAndOwnerIdNot(newBookingDto.getItemId(), bookerId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " +
                                                         newBookingDto.getItemId() +
                                                         ", которую мог бы забронировать пользователь с id = " +
                                                         bookerId));

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + bookerId));

        Booking booking = BookingMapper.mapToBooking(booker, item, newBookingDto);
        List<Booking> existingBookings = bookingRepository.findByItemIdAndActiveInPeriod(item.getId(),
                booking.getStart(),
                booking.getEnd());
        if (!item.getAvailable() || !existingBookings.isEmpty()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " недоступна");
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus() == newStatus) {
            throw new ValidationException("Передан неправильный новый статус бронирования");
        }
        Item item = booking.getItem();
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ValidationException("Указан неправильный владелец вещи");
        }

        booking.setStatus(newStatus);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        if (!Objects.equals(booking.getBooker().getId(), userId) &&
            !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Просматривать бронирование может только инициатор или владелец вещи");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateString) {
        BookingState state = getBookingStateForSearching(stateString);
        return BookingMapper.mapToBookingDto(bookingRepository.findByBookerIdAndState(userId, state.name()));
    }

    private BookingState getBookingStateForSearching(String stateString) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString.toUpperCase());
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + stateString);
        }
        return state;
    }

    @Override
    public List<BookingDto> getBookingsByItemsOwner(Long ownerId, String stateString) {
        BookingState state = getBookingStateForSearching(stateString);
        List<BookingDto> result = BookingMapper.mapToBookingDto(bookingRepository.findByOwnerIdAndState(ownerId,
                state.name()));
        if (result.isEmpty()) {
            throw new NotFoundException("Не найдено бронирований вещей");
        }
        return result;
    }
}