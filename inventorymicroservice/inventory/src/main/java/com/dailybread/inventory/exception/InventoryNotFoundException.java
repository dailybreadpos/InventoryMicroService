package com.dailybread.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
// This exception is thrown when an inventory item is not found in the database.