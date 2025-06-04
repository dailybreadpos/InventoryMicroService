package com.dailybread.inventory.rest;

import com.dailybread.inventory.entity.Inventory;
import com.dailybread.inventory.service.CloudinaryService;
import com.dailybread.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    // Create product with image
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
            return ResponseEntity.internalServerError().body("Failed to upload image.");
        }
    }

    // Update product with optional image update
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

            String imageUrl = existing.getImage(); // default to old image

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
            return ResponseEntity.internalServerError().body("Failed to update image.");
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }

    // Toggle the 'disabled' status of an inventory item
    @PatchMapping("/{id}/toggle-disable")
    public Inventory toggleDisabled(@PathVariable Long id) {
        Inventory inventory = inventoryService.findById(id);
        inventory.setDisabled(!inventory.isDisabled());
        return inventoryService.save(inventory);
    }

    @GetMapping("/enabled")
    public List<Inventory> getEnabledInventory() {
        return inventoryService.getEnabledItems();
    }

}
