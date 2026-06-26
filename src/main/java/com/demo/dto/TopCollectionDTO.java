package com.demo.dto;

public class TopCollectionDTO {

    private String name;
    private Long products;
    private Long sold;
    private Double revenue;

    public TopCollectionDTO(
            String name,
            Long products,
            Long sold,
            Double revenue
    ) {
        this.name = name;
        this.products = products;
        this.sold = sold;
        this.revenue = revenue;
    }

    public String getName() {
        return name;
    }

    public Long getProducts() {
        return products;
    }

    public Long getSold() {
        return sold;
    }

    public Double getRevenue() {
        return revenue;
    }
}