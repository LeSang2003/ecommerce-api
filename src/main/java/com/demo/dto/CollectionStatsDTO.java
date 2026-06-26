package com.demo.dto;

public class CollectionStatsDTO {

    private Long totalCollections;
    private Long featuredCollections;
    private Long totalProducts;
    private String largestCollection;
    private String bestSellingCollection;
    
    private String highestRevenueCollection;
    public CollectionStatsDTO(
            Long totalCollections,
            Long featuredCollections,
            Long totalProducts,
            String largestCollection,
            String bestSellingCollection,
            String highestRevenueCollection
    ) {
        this.totalCollections = totalCollections;
        this.featuredCollections = featuredCollections;
        this.totalProducts = totalProducts;
        this.largestCollection = largestCollection;
        this.bestSellingCollection = bestSellingCollection;
        this.highestRevenueCollection = highestRevenueCollection;
    }

    public Long getTotalCollections() {
        return totalCollections;
    }

    public Long getFeaturedCollections() {
        return featuredCollections;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public String getLargestCollection() {
        return largestCollection;
    }

    public String getBestSellingCollection() {
      return bestSellingCollection;
    }

    public String getHighestRevenueCollection() {
      return highestRevenueCollection;
    }

}