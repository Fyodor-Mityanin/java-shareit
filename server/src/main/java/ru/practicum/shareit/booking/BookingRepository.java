package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    @Modifying
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateStatus(@NonNull @Param("status") BookingStatus status, @NonNull @Param("id") Long id);

    List<Booking> findAllByBooker_IdOrderByStartDateDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDateDesc(Long userId, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :userId and b.startDate <= :now and b.endDate > :now order by b.startDate desc")
    List<Booking> findCurrentByBookerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.startDate <= :now and b.endDate > :now order by b.startDate desc")
    List<Booking> findCurrentByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :userId and b.endDate < :now order by b.startDate desc")
    List<Booking> findPastByBookerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.endDate < :now order by b.startDate desc")
    List<Booking> findPastByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = :userId and b.startDate > :now order by b.startDate desc")
    List<Booking> findFutureByBookerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.startDate > :now order by b.startDate desc")
    List<Booking> findFutureByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(@NonNull Long userId, @NonNull BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDateDesc(@NonNull Long userId, @NonNull BookingStatus status, Pageable pageable);

    @Query("select b from Booking b where b.id = :bookingId and (b.booker.id = :userId or b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@NonNull Long bookingId, @NonNull Long userId);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and ((b.startDate between :start and :end or b.endDate between :start and :end) " +
            "or (b.startDate <= :start and b.endDate >= :end))")
    List<Booking> findCrossBookings(@NonNull Long itemId, @NonNull LocalDateTime start, @NonNull LocalDateTime end);

    Optional<Booking> findFirstByItem_IdAndItem_Owner_IdAndEndDateLessThanOrderByEndDateDesc(@NonNull Long itemId, @NonNull Long ownerId, @NonNull LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndItem_Owner_IdAndStartDateGreaterThanOrderByStartDateAsc(@NonNull Long itemId, @NonNull Long ownerId, @NonNull LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndBooker_IdAndEndDateLessThan(Long itemId, Long bookerId, LocalDateTime endDate);

    @Query(value = "select b1.* " +
            "from bookings b1 " +
            "inner join (select item_id, MAX(end_time) max_end_time from bookings where end_time < (:now) group by item_id) b2 " +
            "on b1.item_id = b2.item_id and b1.end_time = b2.max_end_time " +
            "where b1.item_id in (:itemIds)", nativeQuery = true)
    List<Booking> findLastItemBookings(@Param("itemIds") @NonNull List<Long> itemIds, @Param("now") @NonNull LocalDateTime now);

    @Query(value = "select b1.* " +
            "from bookings b1 " +
            "inner join (select item_id, MIN(start_time) min_end_time from bookings where start_time > (:now) group by item_id) b2 " +
            "on b1.item_id = b2.item_id and b1.start_time = b2.min_end_time " +
            "where b1.item_id in (:itemIds)", nativeQuery = true)
    List<Booking> findNextItemBookings(@Param("itemIds") @NonNull List<Long> itemIds, @Param("now") @NonNull LocalDateTime now);
}
