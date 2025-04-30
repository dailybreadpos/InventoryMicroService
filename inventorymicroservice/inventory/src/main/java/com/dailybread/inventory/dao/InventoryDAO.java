package com.dailybread.inventory.dao;

import com.dailybread.inventory.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryDAO {
    List<Inventory> findAll();

    Optional<Inventory> findById(Long id);

    Inventory save(Inventory inventory);

    void deleteById(Long id);
}
