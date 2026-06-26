package com.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
  private String customerName;
  private String phone;
  private String address;
  private String paymentMethod;
  private String couponCode;
  private List<OrderItemRequest> items;
}
