package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(String name);

    @Transactional
    @Modifying
    @Query("update Item i set i.isAvailable = :available where i.id = :id")
    void updateAvailable(@NonNull @Param("available") Boolean available, @NonNull @Param("id") Long id);
}
