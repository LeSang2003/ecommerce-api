package com.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;

    private Double totalPrice;

    private String status;

    private String paymentMethod;

    // THÊM
    private String transactionNo;

    private String bankCode;

    private LocalDateTime paymentTime;

    private String couponCode;

    private Double discountPercent;

    private LocalDateTime createAt;

    private List<OrderItemResponse> items;
}