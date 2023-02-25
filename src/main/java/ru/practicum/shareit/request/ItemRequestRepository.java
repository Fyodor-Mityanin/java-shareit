package ru.practicum.shareit.request;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

interface ItemRequestRepository  extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequester_Id(@NonNull Long id);

}
