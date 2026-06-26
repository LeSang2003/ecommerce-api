package com.demo.dto;

import lombok.Data;

@Data
public class WishlistStatsResponse {

    private Long totalWishlists;
    private Long totalItems;
    private Long uniqueUsers;
    private String mostWishedProduct;
    private Double averageItemsPerUser;
}