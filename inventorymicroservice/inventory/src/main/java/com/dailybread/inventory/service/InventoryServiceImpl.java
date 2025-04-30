package com.dailybread.inventory.service;

import com.dailybread.inventory.dao.InventoryDAO;
import com.dailybread.inventory.entity.Inventory;
import com.dailybread.inventory.exception.InventoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDAO inventoryDao;

    @Autowired
    public InventoryServiceImpl(InventoryDAO inventoryDao) {
        this.inventoryDao = inventoryDao;
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryDao.findAll();
    }

    @Override
    public Inventory findById(Long id) {
        return inventoryDao.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Item not found with id " + id));
    }

    @Override
    public Inventory save(Inventory item) {
        return inventoryDao.save(item);
    }

    @Override
    public void delete(Long id) {
        inventoryDao.deleteById(id);
    }
}
