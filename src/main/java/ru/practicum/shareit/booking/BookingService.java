package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.exeptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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


    public BookingDto create(BookingRequestDto bookingRequestDto, Long userId) {
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(
                () -> new ItemNotFoundException("Предмет не найден")
        );
        if (!item.getIsAvailable()) {
            throw new ItemNotAvailableException("Предмет недоступен");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Юзер не найден")
        );
        Booking booking = BookingMapper.toObject(bookingRequestDto, item, user, BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return BookingMapper.toDto(booking);
    }

    public BookingDto approve(Long userId, Long bookingId) {
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
            throw new BookingValidationException("Это не ваша вещь, что бы её распоряжаться");
        }
        bookingRepository.updateStatus(BookingStatus.APPROVED, booking.getId());
        booking.setStatus(BookingStatus.APPROVED);
        return BookingMapper.toDto(booking);
    }
}
