package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderById(Long userId);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(String name);
}
