package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exeptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    public BookingDto create(BookingRequestDto bookingRequestDto, Long bookerId) {
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("Предмет не найден")
        );
        if (!item.getIsAvailable()) {
            throw new ItemNotAvailableException("Предмет недоступен");
        }
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new BookingImpossibleException("Нельзя заказать у самого себя");
        }
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> new UserNotFoundException("Юзер не найден")
        );
        List<Booking> crossBookings = bookingRepository.findCrossBookings(
                bookingRequestDto.getItemId(),
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd()
        );
        if (crossBookings.size() != 0) {
            throw new BookingImpossibleException("На эти даты уже забронено");
        }
        Booking booking = BookingMapper.toObject(bookingRequestDto, item, user, BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);

    }

    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Юзер не найден")
        );
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Букинг не найден")
        );
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingValidationException("Букинг не в статусе 'ОЖИДАНИЕ'");
        }
        if (!booking.getItem().getOwner().equals(user)) {
            throw new BookingPermissionDeniedException("Это не ваша вещь, что бы её распоряжаться");
        }
        if (approved) {
            bookingRepository.updateStatus(BookingStatus.APPROVED, booking.getId());
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            bookingRepository.updateStatus(BookingStatus.REJECTED, booking.getId());
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toDto(booking);
    }

    public BookingDto getOneByIdAndUserId(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserID(bookingId, userId)
                .orElseThrow(
                        () -> new BookingNotFoundException(String.format("Букинг с id %d и юзером %d не найден", bookingId, userId))
                );
        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getAllByBookerAndState(Long userId, BookingState state) {
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDateDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByBookerId(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findPastByBookerId(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByBookerId(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        if (bookings.size() == 0) {
            throw new BookingNotFoundException("Букинги не найдены");
        }
        return BookingMapper.toDtos(bookings);
    }

    public List<BookingDto> getAllByOwnerAndState(Long userId, BookingState state) {
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDateDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        if (bookings.size() == 0) {
            throw new BookingNotFoundException("Букинги не найдены");
        }
        return BookingMapper.toDtos(bookings);
    }
}
