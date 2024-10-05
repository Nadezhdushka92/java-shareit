package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.mapper.BookingMapper.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto addBooking(BookingDto bookingDto, Long bookerId) {
        UserDto userFromDb = checkUserById(bookerId);
        Item itemFromDb = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Отсутсвует вещь с id " + bookingDto.getItemId()));

        if (itemFromDb.getOwner().getId() == bookerId) {
            throw new EntityNotFoundException("Это не владелец вещи");
        }
        if (!itemFromDb.getAvailable()) {
            throw new IncorrectDataException("Бронирование: Вещь недостпуна");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Бронирование: Даты = null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
            || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Бронирование: Проблема в данных");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        return toBookingDto(bookingRepository.save(toBookingDb(bookingDto, itemFromDb, toUser(userFromDb))));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, String approve) {
        checkUserById(ownerId);
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Нет бронирования с Id: " + bookingId)));

        if (!Objects.equals(bookingDto.getItem().getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("Пользователь с id = " + ownerId + " не владелец");
        }

        if (approve.equalsIgnoreCase("true")) {
            if (bookingDto.getStatus().equals(BookingStatus.APPROVED)) {
                throw new IncorrectDataException("Статус Approved");
            }
            bookingDto.setStatus(BookingStatus.APPROVED);
        } else if (approve.equalsIgnoreCase("false")) {
            bookingDto.setStatus(BookingStatus.REJECTED);
        } else {
            throw new IncorrectDataException("Некорректные данные");
        }
        Booking bookingToUpdate = toBookingUpdate(bookingDto, bookingRepository.findById(bookingId).get());
        bookingRepository.save(bookingToUpdate);
        return toBookingDto(bookingToUpdate);
    }

    @Override
    public BookingDto getBookingInfo(Long bookingId, Long userId) {
        checkUserById(userId);
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Нет бронирования с Id: " + bookingId)));
        if (!Objects.equals(bookingDto.getItem().getOwnerId(), userId) && !Objects.equals(bookingDto.getBooker().getId(), userId)) {
            throw new EntityNotFoundException("Пользователь с id = " + userId + " не владелец");
        }
        return bookingDto;
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, BookingState state) {
        checkUserById(userId);
        List<Booking> bookings;
        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndWaitingStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndRejectedStatus(userId, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndCurrentStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndFutureStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndPastStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByBooker_Id(userId, SORT_BY_START_DESC));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, BookingState state) {
        checkUserById(ownerId);
        List<Long> userItemsIds = itemRepository.findByOwner_Id(ownerId, Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (userItemsIds.isEmpty()) {
            throw new IncorrectDataException("Этот метод только для тех кто имеет >1 вещи");
        }
        List<Booking> bookings;
        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndWaitingStatus(userItemsIds, BookingStatus.WAITING, SORT_BY_START_DESC));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndRejectedStatus(userItemsIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndCurrentStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndFutureStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndPastStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItems(userItemsIds, SORT_BY_START_DESC));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private UserDto checkUserById(Long userId) {
        if (userId < 1) {
            throw new IncorrectDataException("Отсутствует пользователь с header-Id : " + userId);
        }
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Отсутвует пользователь с id: " + userId)));
    }

}