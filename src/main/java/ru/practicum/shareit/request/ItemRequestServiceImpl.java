package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exeptions.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
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
    public ItemRequestDto create(@Valid ItemRequestRequestDto itemRequestDto, Long userId) {
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

    public List<ItemRequestDto> findAll(Pageable pageable) {
        Page<ItemRequest> requests = itemRequestRepository.findAll(pageable);
        return ItemRequestMapper.toDtos(requests.toList());
    }

    @Override
    public List<ItemRequestDto> findAll() {
        List<ItemRequest> requests = itemRequestRepository.findAll();
        return ItemRequestMapper.toDtos(requests);
    }
}
