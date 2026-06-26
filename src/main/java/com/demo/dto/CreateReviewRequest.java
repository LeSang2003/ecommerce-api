package com.demo.dto;

import lombok.Data;

@Data
public class CreateReviewRequest {

    private Long productId;

    private Long orderId;

    private Integer rating;

    private String comment;

    private String imageUrl;
}