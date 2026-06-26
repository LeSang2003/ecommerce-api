package com.demo.dto;

import com.demo.model.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusStatsResponse {
    private OrderStatus status;
    private Long total;
}
