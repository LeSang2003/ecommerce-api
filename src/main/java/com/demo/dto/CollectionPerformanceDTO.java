package com.demo.dto;

public class CollectionPerformanceDTO {

    private String collectionName;
    private Long totalSold;
    private Double totalRevenue;

    public CollectionPerformanceDTO(
            String collectionName,
            Long totalSold,
            Double totalRevenue
    ) {
        this.collectionName = collectionName;
        this.totalSold = totalSold;
        this.totalRevenue = totalRevenue;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }
}