package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EmptyException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.comment.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        UserDto userFromDb = checkUserById(userId);

        List<Item> userItems = new ArrayList<>(itemRepository.findByOwner_Id(userFromDb.getId(), Sort.by(Sort.Direction.ASC, "id")));
        List<CommentDto> commentsToUserItems = commentRepository.findAllByItemsUserId(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        List<BookingDto> bookingsToUserItems = getOwnerBooking(userId);

        Map<Item, List<BookingDto>> itemsWithBookingsMap = new HashMap<>();
        Map<Item, List<CommentDto>> itemsWithCommentsMap = new HashMap<>();

        for (Item i : userItems) {
            itemsWithCommentsMap.put(i, commentsToUserItems.stream()
                    .filter(c -> c.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList()));
            itemsWithBookingsMap.put(i, bookingsToUserItems.stream()
                    .filter(b -> b.getItem().getId().equals(i.getId()))
                    .collect(Collectors.toList()));
        }

        log.debug("Получение списка всех Items юзера с id:{}\", userId", userFromDb.getId());
        List<ItemDto> results = new ArrayList<>();
        for (Item i : userItems) {
            results.add(toItemDtoWithBookingsAndComments(i, itemsWithBookingsMap.get(i), itemsWithCommentsMap.get(i)));
        }

        return results;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.debug("Поиск вещи : {} ", text);
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        checkUserById(userId);
        log.info("Поиск вещи c id:{} владелец с id: {}", itemId, userId);
        List<CommentDto> commentsForItem = commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<BookingDto> bookingsForItem = getOwnerBooking(userId)
                .stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());

        if (!bookingsForItem.isEmpty() && !commentsForItem.isEmpty()) {
            return toItemDtoWithBookingsAndComments(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Отсутствует вещь с Id: " + itemId)), bookingsForItem, commentsForItem);
        } else if (!bookingsForItem.isEmpty()) {
            return toItemDtoWithBookings(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Отсутствует вещь с Id: " + itemId)), bookingsForItem);
        } else if (!commentsForItem.isEmpty()) {
            return toItemDtoWithComments(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Отсутствует вещь с Id: " + itemId)), commentsForItem);
        } else {
            return toItemDto(itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Отсутствует вещь с Id: " + itemId)));
        }
    }

    @Override
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        log.info("Добавление item : {}; Владелец: {}", itemDto, userId);
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyException("Null в ItemDto");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyException("Empty в ItemDto");
        }
        UserDto userFromDb = checkUserById(userId);
        return toItemDto(itemRepository.save(toItemDb(itemDto, toUser(userFromDb))));
    }

    @Override
    public ItemDto update(Long userId, ItemDto item) {
        checkUserById(userId);
        checkItemById(item.getId());
        log.info("Обновление item : {}; для пользователя {}", item, userId);
        Item itemToUpdate = toItemUpdate(item, itemRepository.findById(item.getId())
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with Id: " + item.getId())));
        itemRepository.save(itemToUpdate);
        return toItemDto(itemToUpdate);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto checkItemOwner(Long ownerId, Long itemId) {
        ItemDto itemDto = toItemDto(itemRepository.findById(itemId).get());
        if (!Objects.equals(itemDto.getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("User with id: " + ownerId + " is not owner");
        }
        return itemDto;
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Текст отзыва не может быть Empty");
        }
        UserDto author = checkUserById(userId);
        List<BookingDto> bookings = bookingRepository.findAllByUserIdAndItemIdAndEndDateIsPassed(userId, itemId, LocalDateTime.now())
                .stream()
                .map(BookingMapper::toBookingDto)
                .toList();
        if (bookings.isEmpty()) {
            throw new ValidationException("Данный пользователь не арендовывал вещь");
        }
        ItemDto item = getItemById(userId, itemId);
        commentDto = toCommentDto(commentRepository.save(CommentMapper.toCommentDb(commentDto, toUser(author), toItem(item))));
        return commentDto;
    }

    private UserDto checkUserById(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Отсутствует пользователь с header-Id : " + userId);
        }
        return toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Отсутствует пользователь с Id : " + userId)));
    }

    private void checkItemById(long itemId) {
        if (itemRepository.findById(itemId) == null) {
            throw new NotFoundException("Отсутствует вещь с id: " + itemId);
        }
    }

    private List<BookingDto> getOwnerBooking(Long ownerId) {
        return bookingRepository.findAllByItem_Owner_Id(ownerId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}