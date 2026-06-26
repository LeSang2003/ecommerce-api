package com.demo.dto.lookbook;

import java.util.List;

public class LookbookRequest {

    private String title;

    private String slug;

    private String season;

    private Integer year;

    private String description;

    private String coverImage;

    private Boolean featured;

    private List<LookbookImageRequest> images;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getSlug() {
      return slug;
    }

    public void setSlug(String slug) {
      this.slug = slug;
    }

    public String getSeason() {
      return season;
    }

    public void setSeason(String season) {
      this.season = season;
    }

    public Integer getYear() {
      return year;
    }

    public void setYear(Integer year) {
      this.year = year;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getCoverImage() {
      return coverImage;
    }

    public void setCoverImage(String coverImage) {
      this.coverImage = coverImage;
    }

    public Boolean getFeatured() {
      return featured;
    }

    public void setFeatured(Boolean featured) {
      this.featured = featured;
    }

    public List<LookbookImageRequest> getImages() {
      return images;
    }

    public void setImages(List<LookbookImageRequest> images) {
      this.images = images;
    }

    // generate getter setter
    
    
}