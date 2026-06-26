package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
  private Long productId;
  private String productName;
  private Double price;
  private Integer quantity;
  private Double subtotal;
}
