package com.demo.dto.lookbook;

public class LookbookImageResponse {

    private Long id;

    private String imageUrl;

    private Integer displayOrder;

    private String layoutType;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
      return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
      this.displayOrder = displayOrder;
    }

    public String getLayoutType() {
      return layoutType;
    }

    public void setLayoutType(String layoutType) {
      this.layoutType = layoutType;
    }

    // getter setter
    
}