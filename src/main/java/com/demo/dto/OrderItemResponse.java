package com.demo.dto;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Long productId;

    private String productName;

    private Integer quantity;

    private Double price;

    private String imageUrl;

    private String color;

    private String size;
}