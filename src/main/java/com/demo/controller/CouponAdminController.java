package com.demo.controller;

import com.demo.model.DiscountCoupon;
import com.demo.repository.DiscountCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CouponAdminController {

    private final DiscountCouponRepository couponRepository;

    // GET ALL
    @GetMapping
    public Page<DiscountCoupon> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "") String keyword
    ) {
        if (!keyword.isBlank()) {
        return couponRepository.findByCodeContainingIgnoreCase(
                keyword,
                PageRequest.of(page, size)
        );
        }

        return couponRepository.findAll(
            PageRequest.of(page, size)
        );
    }

    // CREATE
    @PostMapping
    public DiscountCoupon create(
            @RequestBody DiscountCoupon coupon
    ) {
        if (coupon.getUsedCount() == null) {
            coupon.setUsedCount(0);
        }

        if (coupon.getActive() == null) {
            coupon.setActive(true);
        }

        return couponRepository.save(coupon);
    }

    // UPDATE
    @PutMapping("/{id}")
    public DiscountCoupon update(
            @PathVariable Long id,
            @RequestBody DiscountCoupon updated
    ) {
        DiscountCoupon coupon = couponRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Coupon not found"));

        coupon.setCode(updated.getCode());
        coupon.setDiscountPercent(updated.getDiscountPercent());
        coupon.setActive(updated.getActive());
        coupon.setExpiredAt(updated.getExpiredAt());
        coupon.setMinOrderValue(updated.getMinOrderValue());
        coupon.setMaxUsage(updated.getMaxUsage());

        return couponRepository.save(coupon);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        couponRepository.deleteById(id);
    }

    // TOGGLE ACTIVE
    @PutMapping("/{id}/toggle")
    public DiscountCoupon toggle(
            @PathVariable Long id
    ) {
        DiscountCoupon coupon = couponRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Coupon not found"));

        coupon.setActive(!coupon.getActive());

        return couponRepository.save(coupon);
    }
}