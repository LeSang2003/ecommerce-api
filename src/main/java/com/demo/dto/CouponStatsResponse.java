package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponStatsResponse {

    private Long totalCoupons;

    private Long activeCoupons;

    private Long usedCoupons;

    private String mostUsedCoupon;
}