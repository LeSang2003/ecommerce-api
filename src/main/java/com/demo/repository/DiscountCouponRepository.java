package com.demo.repository;

import com.demo.model.DiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface DiscountCouponRepository
        extends JpaRepository<DiscountCoupon, Long> {

    Optional<DiscountCoupon> findByCode(String code);

    long countByActiveTrue();

    long countByUsedCountGreaterThan(int usedCount);

    Page<DiscountCoupon> findAll(Pageable pageable);

    Page<DiscountCoupon> findByCodeContainingIgnoreCase(String code, Pageable pageable);
}