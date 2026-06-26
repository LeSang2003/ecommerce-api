package com.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponse {

    private Long orderId;

    private String customerName;

    private String phone;

    private String address;

    private String paymentMethod;

    private String couponCode;

    private Double discountPercent;

    private Double totalPrice;

    private String status;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}