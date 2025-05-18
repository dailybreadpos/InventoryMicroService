package com.dailybread.inventory.service;

import com.dailybread.inventory.entity.Inventory;

import java.util.List;

public interface InventoryService {
    List<Inventory> findAll();

    Inventory findById(Long id);

    Inventory save(Inventory item);

    void delete(Long id);

    List<Inventory> getEnabledItems();
}
