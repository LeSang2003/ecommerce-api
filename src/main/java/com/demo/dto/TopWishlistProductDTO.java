package com.demo.dto;

public class TopWishlistProductDTO {

    private String name;
    private Long count;

    public TopWishlistProductDTO(
            String name,
            Long count
    ) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public Long getCount() {
        return count;
    }
}