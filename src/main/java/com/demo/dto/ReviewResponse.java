package com.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {

    private Long id;

    private Long userId;
    
    private String username;

    private String avatar;

    private Integer rating;

    private String comment;

    private String imageUrl;

    private LocalDateTime createdAt;
    

    
}