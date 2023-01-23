package ru.practicum.shareit.booking;

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

    List<Booking> findAllByBooker_IdOrderByStartDateDesc(Long userId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDateDesc(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId and b.startDate <= :now and b.endDate > :now order by b.startDate desc")
    List<Booking> findCurrentByBookerId(@NonNull Long userId, @NonNull LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.startDate <= :now and b.endDate > :now order by b.startDate desc")
    List<Booking> findCurrentByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now);

    @Query("select b from Booking b where b.booker.id = :userId and b.endDate < :now order by b.startDate desc")
    List<Booking> findPastByBookerId(@NonNull Long userId, @NonNull LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.endDate < :now order by b.startDate desc")
    List<Booking> findPastByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now);

    @Query("select b from Booking b where b.booker.id = :userId and b.startDate > :now order by b.startDate desc")
    List<Booking> findFutureByBookerId(@NonNull Long userId, @NonNull LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = :userId and b.startDate > :now order by b.startDate desc")
    List<Booking> findFutureByOwnerId(@NonNull Long userId, @NonNull LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(@NonNull Long userId, @NonNull BookingStatus status);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDateDesc(@NonNull Long userId, @NonNull BookingStatus status);

    @Query("select b from Booking b where b.id = :bookingId and (b.booker.id = :userId or b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserID(@NonNull Long bookingId, @NonNull Long userId);
}
