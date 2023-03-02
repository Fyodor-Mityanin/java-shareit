package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.error.exeptions.CommentImpossibleException;
import ru.practicum.shareit.error.exeptions.ItemNotFoundException;
import ru.practicum.shareit.error.exeptions.ItemRequestNotFoundException;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemValidation itemValidation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(
            ItemValidation itemValidation,
            ItemRepository itemRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            CommentRepository commentRepository,
            ItemRequestRepository itemRequestRepository
    ) {
        this.itemValidation = itemValidation;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        itemDto.setOwner(userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        itemValidation.validateCreation(itemDto);
        itemDto.setOwner(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(
                    () -> new ItemRequestNotFoundException(String.format("Реквест с id %d не найден", userId))
            );
        }
        Item item = itemRepository.save(ItemMapper.toObject(itemDto, user, itemRequest));
        itemDto.setId(item.getId());
        return itemDto;
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        itemDto.setOwner(userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        Item updatedItem = itemValidation.validateUpdateAndGet(itemDto);
        ItemDto updatedItemDto = ItemMapper.toDto(updatedItem);
        if (itemDto.getAvailable() != null && updatedItemDto.getAvailable() != itemDto.getAvailable()) {
            updatedItemDto.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null && !Objects.equals(updatedItemDto.getDescription(), itemDto.getDescription())) {
            updatedItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getName() != null && !Objects.equals(updatedItemDto.getName(), itemDto.getName())) {
            updatedItemDto.setName(itemDto.getName());
        }
        updatedItem = ItemMapper.toObject(updatedItemDto, user, null);
        updatedItem = itemRepository.save(updatedItem);
        return ItemMapper.toDto(updatedItem);
    }

    public ItemDto getByIdWithBookings(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(
                        () -> new ItemNotFoundException(String.format("Предмет с id %d не найден", itemId))
                );
        List<CommentDto> commentDtos = commentRepository.findByItemIdWithAuthor(itemId);
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.getComments().addAll(commentDtos);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            fillBookings(itemDto);
        }
        return itemDto;
    }

    public List<ItemDto> getAllByUserId(Long userId) {
        List<ItemDto> itemDtos = ItemMapper.toDtos(itemRepository.findAllByOwnerIdOrderById(userId));
        fillBookings(itemDtos);
        return itemDtos;
    }

    public List<ItemDto> searchByName(String text) {
        List<Item> items = itemRepository.findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text);
        return ItemMapper.toDtos(items);
    }

    private void fillBookings(ItemDto itemDto) {
        bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndEndDateLessThanOrderByEndDateDesc(
                itemDto.getId(),
                itemDto.getOwner(),
                LocalDateTime.now()
        ).ifPresent(booking -> {
            ItemBookingDto lastItemBooking = ItemBookingDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .build();
            itemDto.setLastBooking(lastItemBooking);
        });
        bookingRepository.findFirstByItem_IdAndItem_Owner_IdAndStartDateGreaterThanOrderByStartDateAsc(
                itemDto.getId(),
                itemDto.getOwner(),
                LocalDateTime.now()
        ).ifPresent(booking -> {
            ItemBookingDto nextItemBooking = ItemBookingDto.builder()
                    .id(booking.getId())
                    .bookerId(booking.getBooker().getId())
                    .build();
            itemDto.setNextBooking(nextItemBooking);
        });
    }

    private void fillBookings(List<ItemDto> itemDtos) {
        List<Long> itemIds = itemDtos.stream()
                .mapToLong(ItemDto::getId)
                .boxed()
                .collect(Collectors.toList());
        bookingRepository.findLastItemBookings(itemIds, LocalDateTime.now())
                .forEach(booking -> itemDtos.stream().filter(i -> Objects.equals(i.getId(), booking.getItem().getId()))
                        .forEach(i -> i.setLastBooking(ItemBookingDto.builder().id(booking.getId())
                                .bookerId(booking.getBooker().getId())
                                .build())
                        )
                );
        bookingRepository.findNextItemBookings(itemIds, LocalDateTime.now())
                .forEach(booking -> itemDtos.stream().filter(i -> Objects.equals(i.getId(), booking.getItem().getId()))
                        .forEach(i -> i.setNextBooking(ItemBookingDto.builder().id(booking.getId())
                                .bookerId(booking.getBooker().getId())
                                .build())
                        )
                );
    }

    public CommentDto createComment(Long userId, Long itemId, String text) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException(String.format("Предмет с id %d не найден", itemId))
        );
        if (bookingRepository.findFirstByItem_IdAndBooker_IdAndEndDateLessThan(item.getId(), author.getId(), LocalDateTime.now()).isEmpty()) {
            throw new CommentImpossibleException("Юзер не букал предмет");
        }
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setText(text);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toDto(savedComment, author);

    }
}
