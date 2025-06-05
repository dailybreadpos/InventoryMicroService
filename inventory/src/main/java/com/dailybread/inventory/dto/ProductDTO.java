package com.dailybread.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation to generate getters, setters, toString, equals, and
      // hashCode
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor
@AllArgsConstructor // Lombok annotation to generate an all-argument constructor
@Builder // Lombok annotation to provide a builder API for object creation
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private double price;
    private int stock;
    private String category;
}
