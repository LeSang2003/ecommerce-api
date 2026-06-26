package com.demo.dto.lookbook;

import java.util.List;

public class CreateLookbookRequest {

    private String title;

    private String slug;

    private String season;

    private Integer year;

    private String description;

    private String coverImage;

    private Boolean featured;

    private List<CreateLookbookImageRequest> images;

    private List<CreateLookbookSectionRequest> sections;
    
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

    public List<CreateLookbookImageRequest> getImages() {
      return images;
    }

    public void setImages(List<CreateLookbookImageRequest> images) {
      this.images = images;
    }

    public List<CreateLookbookSectionRequest> getSections() {
      return sections;
    }

    public void setSections(List<CreateLookbookSectionRequest> sections) {
      this.sections = sections;
    }


    // getter setter
    
}