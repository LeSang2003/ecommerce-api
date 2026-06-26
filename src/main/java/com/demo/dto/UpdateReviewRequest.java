package com.demo.dto;

import lombok.Data;

@Data
public class UpdateReviewRequest {

    private Integer rating;

    private String comment;

    private String imageUrl;
}