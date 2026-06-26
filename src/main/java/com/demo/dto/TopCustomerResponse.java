package com.demo.dto;
import lombok.Data;

@Data
public class TopCustomerResponse {
  private Long userId;
  private String email;
  private Double totalSpent;
}
