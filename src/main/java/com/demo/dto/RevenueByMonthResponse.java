package com.demo.dto;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByMonthResponse {
    private String month;
    private double revenue;
}
