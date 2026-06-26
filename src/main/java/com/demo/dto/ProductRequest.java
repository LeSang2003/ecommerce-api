package com.demo.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;

public class ProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    // CHO PHÉP NULL
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String imageUrl;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be >= 0")
    private Integer stock;

    @NotNull(message = "Category is required")
    private Long categoryId;

    // KHÔNG BẮT BUỘC
    private List<Long> colorIds = new ArrayList<>();

    // KHÔNG BẮT BUỘC
    private List<Long> sizeIds = new ArrayList<>();

    private Long collectionId;

    private String material;

    private String gender;
    // =========================
    // GETTER SETTER
    // =========================

public Long getCollectionId() {
        return collectionId;
    }
    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getColorIds() {
        return colorIds;
    }

    public void setColorIds(List<Long> colorIds) {
        this.colorIds = colorIds;
    }

    public List<Long> getSizeIds() {
        return sizeIds;
    }

    public void setSizeIds(List<Long> sizeIds) {
        this.sizeIds = sizeIds;
    }
}