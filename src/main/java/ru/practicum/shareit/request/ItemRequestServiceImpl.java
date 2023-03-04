package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeptions.ItemRequestNotFoundException;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(
            UserRepository userRepository,
            ItemRequestRepository itemRequestRepository
    ) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDto create(ItemRequestRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Юзер не найден")
        );
        ItemRequest itemRequest = ItemRequestMapper.toObject(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByOwner(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Юзер не найден")
        );
        List<ItemRequest> requests = itemRequestRepository.findByRequester_Id(userId);
        return ItemRequestMapper.toDtos(requests);
    }

    public List<ItemRequestDto> findAllExceptRequester(Long userId, Pageable pageable) {
        List<ItemRequest> requests = itemRequestRepository.findByRequester_IdNotOrderByCreatedDesc(userId, pageable);
        return ItemRequestMapper.toDtos(requests);
    }

    @Override
    public ItemRequestDto getOneById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("Юзер с id %d не найден", userId))
        );
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ItemRequestNotFoundException(String.format("Запрос с id %d не найден", requestId))
        );
        return ItemRequestMapper.toDto(itemRequest);
    }
}
