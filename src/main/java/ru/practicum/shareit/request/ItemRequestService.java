package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllByOwner(Long userId);

    List<ItemRequestDto> findAllExceptRequester(Long userId, Pageable pageable);

    ItemRequestDto getOneById(Long userId, Long requestId);
}
