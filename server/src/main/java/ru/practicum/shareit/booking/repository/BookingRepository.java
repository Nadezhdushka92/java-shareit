package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

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

    //ALL
    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    //STAT Wait, cancel, reject
    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status = :waiting")
    List<Booking> findAllByBookerIdAndWaitingStatus(Long bookerId, BookingStatus waiting, Sort sort);

    //STAT Wait, cancel, reject
    @Query("select b from Booking b where b.booker.id = :bookerId AND b.status IN :rejected")
    List<Booking> findAllByBookerIdAndRejectedStatus(Long bookerId, List<BookingStatus> rejected, Sort sort);

    //CURRENT
    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start < :now AND b.end > :now ")
    List<Booking> findAllByBookerIdAndCurrentStatus(Long bookerId, LocalDateTime now, Sort sort);

    //FUTURE
    @Query("select b from Booking b where b.booker.id = :bookerId AND b.start > :now ")
    List<Booking> findAllByBookerIdAndFutureStatus(Long bookerId, LocalDateTime now, Sort sort);

    //PAST
    @Query("select b from Booking b where b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findAllByBookerIdAndPastStatus(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId,
                                                               Long itemId,
                                                               BookingStatus status,
                                                               LocalDateTime endDate);

    @Query("""
            select b
            from Booking as b
            join b.item as i
            where i.id = ?1
                and not(b.start > ?3 or b.end < ?2)
            """)
    List<Booking> findByItemIdAndActiveInPeriod(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("""
            select b
            from Booking as b
            join b.booker as br
            where br.id = ?1 and (
               ?2 = 'ALL'
               or
               ?2 in ('WAITING', 'REJECTED', 'APPROVED') and ?2 = b.status
               or
               ?2 = 'CURRENT' and ?#{T(java.time.LocalDateTime).now()} between b.start and b.end
               or
               ?2 = 'FUTURE' and ?#{T(java.time.LocalDateTime).now()} < b.start
               or
               ?2 = 'PAST' and b.status = 'APPROVED' and ?#{T(java.time.LocalDateTime).now()} > b.end
            )
            order by b.end desc""")
    List<Booking> findByBookerIdAndState(Long bookerId, String state);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o
            where o.id = ?1 and (
               ?2 = 'ALL'
               or
               ?2 in ('WAITING', 'REJECTED', 'APPROVED') and ?2 = b.status
               or
               ?2 = 'CURRENT' and ?#{T(java.time.LocalDateTime).now()} between b.start and b.end
               or
               ?2 = 'FUTURE' and ?#{T(java.time.LocalDateTime).now()} < b.start
               or
               ?2 = 'PAST' and b.status = 'APPROVED' and ?#{T(java.time.LocalDateTime).now()} > b.end
            )
            order by b.end desc""")
    List<Booking> findByOwnerIdAndState(Long ownerId, String state);

    @Query("""
            select b
            from Booking as b
            join b.item as i on i = ?1
            where ?#{T(java.time.LocalDateTime).now()} < b.start
                and b.status <> 'REJECTED'
            """)
    Page<Booking> getNextBookings(Item item, Pageable pageable);

    @Query("""
            select b
            from Booking as b
            join b.item as i on i = ?1
            where ?#{T(java.time.LocalDateTime).now()} >= b.start
                and b.status = 'APPROVED'
            """)
    Page<Booking> getLastBookings(Item item, Pageable pageable);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o on o.id = ?1
                and b.start = (
                    select max(bb.start)
                    from Booking as bb
                    where bb.item = b.item
                        and bb.start <= ?2
                )
            """)
    List<Booking> findNearestPrevBookingsByItemOwner(Long ownerId, LocalDateTime date);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o on o.id = ?1
                and b.start = (
                    select min(bb.start)
                    from Booking as bb
                    where bb.item = b.item
                        and bb.start > ?2
                )
            """)
    List<Booking> findNearestNextBookingsByItemOwner(Long ownerId, LocalDateTime now);
}