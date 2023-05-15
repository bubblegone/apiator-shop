package com.apiator.shop.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i where i.id = ?1")
    Optional<Item> getItemById(long id);

    @Query("update Item i set i.count = ?2 where i.id = ?1")
    void updateItemCountById(long id, int count);
}
