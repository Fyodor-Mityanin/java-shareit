package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(@Valid ItemRequestRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllByOwner(Long userId);

    List<ItemRequestDto> findAll(Pageable pageable);

    List<ItemRequestDto> findAll();
}
