package com.dailybread.inventory.dao;

import com.dailybread.inventory.entity.Inventory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InventoryDAOImpl implements InventoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Inventory> findAll() {
        String jpql = "SELECT i FROM Inventory i";
        return entityManager.createQuery(jpql, Inventory.class).getResultList();
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        Inventory inventory = entityManager.find(Inventory.class, id);
        return Optional.ofNullable(inventory);
    }

    @Override
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            entityManager.persist(inventory);
            return inventory;
        } else {
            return entityManager.merge(inventory);
        }
    }

    @Override
    public void deleteById(Long id) {
        Inventory inventory = entityManager.find(Inventory.class, id);
        if (inventory != null) {
            entityManager.remove(inventory);
        }
    }
}
