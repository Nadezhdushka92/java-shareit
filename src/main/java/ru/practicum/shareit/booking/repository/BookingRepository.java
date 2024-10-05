package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookerId(Long bookerId);

    //ALL
    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    //STAT Wait, cancel, reject
    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    //CURRENT
    List<Booking> findAllByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    //FUTURE
    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    //PAST
    List<Booking> findAllByItem_Owner_IdEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    //ALL
    List<Booking> findAllByItem_Booker_IdOrderByStartDesc(Long bookerId);

    //STAT Wait, cancel, reject
    List<Booking> findAllByItem_Booker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    //CURRENT
    List<Booking> findAllByItem_Booker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long bookerId, LocalDateTime now);

    //FUTURE
    List<Booking> findAllByItem_Booker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    //PAST
    List<Booking> findAllByItem_Booker_IdEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

}