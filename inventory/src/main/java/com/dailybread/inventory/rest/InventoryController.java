package com.dailybread.inventory.rest;

import com.dailybread.inventory.entity.Inventory;
import com.dailybread.inventory.service.CloudinaryService;
import com.dailybread.inventory.service.InventoryService;
import com.dailybread.inventory.dto.ProductDTO; // Import the new DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors; // Import Collectors for stream operations

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Retrieves all inventory items. This endpoint is typically for administrative
     * use.
     * 
     * @return A list of all Inventory entities.
     */
    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.findAll();
    }

    /**
     * Retrieves a single inventory item by its ID. This endpoint is typically for
     * administrative use.
     * 
     * @param id The ID of the inventory item.
     * @return The Inventory entity.
     */
    @GetMapping("/{id}")
    public Inventory getById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    /**
     * Creates a new inventory item, including handling image uploads.
     * 
     * @param name        The name of the product.
     * @param description The description of the product.
     * @param price       The price of the product.
     * @param stock       The stock quantity of the product.
     * @param category    The category of the product.
     * @param imageFile   The image file for the product (optional).
     * @return ResponseEntity with the saved Inventory item or an error message.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Upload image to Cloudinary if provided
            String imageUrl = imageFile != null && !imageFile.isEmpty()
                    ? cloudinaryService.uploadFile(imageFile)
                    : null;

            // Create and save the new Inventory item
            Inventory item = new Inventory();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setStock(stock);
            item.setCategory(category);
            item.setImage(imageUrl);
            item.setDisabled(false); // New items are enabled by default

            return ResponseEntity.ok(inventoryService.save(item));

        } catch (IOException e) {
            // Handle image upload failure
            return ResponseEntity.internalServerError().body("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Updates an existing inventory item, with optional image update.
     * 
     * @param id          The ID of the inventory item to update.
     * @param name        The new name of the product.
     * @param description The new description of the product.
     * @param price       The new price of the product.
     * @param stock       The new stock quantity of the product.
     * @param category    The new category of the product.
     * @param imageFile   The new image file for the product (optional).
     * @return ResponseEntity with the updated Inventory item or an error message.
     */
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
            // Find the existing inventory item
            Inventory existing = inventoryService.findById(id);

            String imageUrl = existing.getImage(); // Default to the existing image URL

            // Upload new image if provided and update the URL
            if (imageFile != null && !imageFile.isEmpty()) {
                imageUrl = cloudinaryService.uploadFile(imageFile);
            }

            // Update item details
            existing.setName(name);
            existing.setDescription(description);
            existing.setPrice(price);
            existing.setStock(stock);
            existing.setCategory(category);
            existing.setImage(imageUrl);

            return ResponseEntity.ok(inventoryService.save(existing));

        } catch (IOException e) {
            // Handle image update failure
            return ResponseEntity.internalServerError().body("Failed to update image: " + e.getMessage());
        }
    }

    /**
     * Deletes an inventory item by its ID.
     * 
     * @param id The ID of the inventory item to delete.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }

    /**
     * Toggles the 'disabled' status of an inventory item. This is typically for
     * administrative use.
     * 
     * @param id The ID of the inventory item to toggle.
     * @return The updated Inventory entity.
     */
    @PatchMapping("/{id}/toggle-disable")
    public Inventory toggleDisabled(@PathVariable Long id) {
        Inventory inventory = inventoryService.findById(id);
        inventory.setDisabled(!inventory.isDisabled());
        return inventoryService.save(inventory);
    }

    /**
     * Retrieves all enabled inventory items. This endpoint can be used by both
     * admin and cashier.
     * 
     * @return A list of enabled Inventory entities.
     */
    @GetMapping("/enabled")
    public List<Inventory> getEnabledInventory() {
        return inventoryService.getEnabledItems();
    }

    /**
     * NEW ENDPOINT FOR CASHIER:
     * Retrieves a list of enabled products with a simplified data structure
     * (ProductDTO).
     * This endpoint is designed for the cashier frontend, providing only necessary
     * fields.
     * 
     * @return A list of ProductDTO objects representing enabled products.
     */
    @GetMapping("/products") // You can choose a different path like "/cashier-products" if preferred
    public List<ProductDTO> getProductsForCashier() {
        // Fetch all enabled inventory items and map them to ProductDTOs
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
                .collect(Collectors.toList()); // Collect the DTOs into a list
    }
}
