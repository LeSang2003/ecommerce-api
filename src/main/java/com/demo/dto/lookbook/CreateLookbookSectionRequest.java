package com.demo.dto.lookbook;

import java.util.List;

public class CreateLookbookSectionRequest {

    private Integer displayOrder;

    private String type;

    private String title;

    private String content;

    private String videoUrl;

    private List<CreateLookbookImageRequest> images;

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<CreateLookbookImageRequest> getImages() {
        return images;
    }

    public void setImages(List<CreateLookbookImageRequest> images) {
        this.images = images;
    }
}