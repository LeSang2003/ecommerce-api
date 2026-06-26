package com.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Double discountPercent;

    private Boolean active = true;

    private LocalDateTime expiredAt;

    // đơn tối thiểu
    private Double minOrderValue;

    // số lượt tối đa
    private Integer maxUsage;

    // số lượt đã dùng
    private Integer usedCount = 0;
}