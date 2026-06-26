package com.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {
  private List<CartItemDTO> items;
  private Double totalPrice;
}
