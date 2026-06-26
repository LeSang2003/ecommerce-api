package com.demo.controller;

import com.demo.dto.CouponStatsResponse;
import com.demo.dto.CouponValidationResponse;
import com.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CouponController {

    private final OrderService orderService;

    // VALIDATE COUPON (USER CHECKOUT)
    @GetMapping("/validate")
    public CouponValidationResponse validateCoupon(
            @RequestParam String code,
            @RequestParam Double totalPrice
    ) {
        return orderService.validateCoupon(code, totalPrice);
    }

    // COUPON STATS
    @GetMapping("/stats")
    public CouponStatsResponse getStats() {
        return orderService.getCouponStats();
    }
}