package com.dailybread.inventory.rest;

import com.dailybread.inventory.entity.Inventory;
import com.dailybread.inventory.service.CloudinaryService;
import com.dailybread.inventory.service.InventoryService;
import com.dailybread.inventory.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/{id}")
    public Inventory getById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            String imageUrl = imageFile != null && !imageFile.isEmpty()
                    ? cloudinaryService.uploadFile(imageFile)
                    : null;

            Inventory item = new Inventory();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStock(stock);
            item.setCategory(category);
            item.setImage(imageUrl);
            item.setDisabled(false);

            return ResponseEntity.ok(inventoryService.save(item));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            Inventory existing = inventoryService.findById(id);

            String imageUrl = existing.getImage();

            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(imageFile);
            }

            existing.setName(name);
            existing.setDescription(description);
            existing.setPrice(price);
            existing.setStock(stock);
            existing.setCategory(category);
            existing.setImage(imageUrl);

            return ResponseEntity.ok(inventoryService.save(existing));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to update image: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }

    @PatchMapping("/{id}/toggle-disable")
    public Inventory toggleDisabled(@PathVariable Long id) {
        System.out.println("INVENTORY_CONTROLLER: Received toggle-disable request for ID: " + id);
        try {
            Inventory inventory = inventoryService.findById(id);
            System.out.println("INVENTORY_CONTROLLER: Item found - ID: " + inventory.getId()
                    + ", Current disabled status: " + inventory.isDisabled());
            inventory.setDisabled(!inventory.isDisabled());
            Inventory updatedInventory = inventoryService.save(inventory);
            System.out.println(
                    "INVENTORY_CONTROLLER: Item saved - New disabled status: " + updatedInventory.isDisabled());
            return updatedInventory;
        } catch (Exception e) {
            System.err.println(
                    "INVENTORY_CONTROLLER: Error toggling disable status for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception so frontend gets an error response
        }
    }

    @GetMapping("/enabled")
    public List<Inventory> getEnabledInventory() {
        return inventoryService.getEnabledItems();
    }

    @GetMapping("/products")
    public List<ProductDTO> getProductsForCashier() {
        return inventoryService.getEnabledItems().stream()
                .map(item -> ProductDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .image(item.getImage())
                        .price(item.getPrice())
                        .stock(item.getStock())
                        .category(item.getCategory())
                        .build())
                .collect(Collectors.toList());
    }
}
