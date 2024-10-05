package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status = :waiting")
    List<Booking> findAllByBookerIdAndWaitingStatus(Long bookerId, BookingStatus waiting, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status IN :rejected")
    List<Booking> findAllByBookerIdAndRejectedStatus(Long bookerId, List<BookingStatus> rejected, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start < :now AND b.end > :now ")
    List<Booking> findAllByBookerIdAndCurrentStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start > :now ")
    List<Booking> findAllByBookerIdAndFutureStatus(Long bookerId, LocalDateTime now, Sort sort);

    @Query("select b from Booking b where b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findAllByBookerIdAndPastStatus(Long bookerId, LocalDateTime now, Sort sort);
}