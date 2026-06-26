package com.demo.model;

import java.util.ArrayList;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
@Entity
@Table(name = "lookbook_sections")
public class LookbookSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    private SectionType type;


     private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookbook_id")
    @JsonBackReference
    private Lookbook lookbook;

    @OneToMany(
        mappedBy = "section",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    @JsonManagedReference("section-images")
    private List<LookbookImage> images = new ArrayList<>();
    public LookbookSection() {
    }
    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Integer getDisplayOrder() {
      return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
      this.displayOrder = displayOrder;
    }

    public SectionType getType() {
      return type;
    }

    public void setType(SectionType type) {
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

    public Lookbook getLookbook() {
      return lookbook;
    }

    public void setLookbook(Lookbook lookbook) {
      this.lookbook = lookbook;
    }

    

   

    public List<LookbookImage> getImages() {
    return images;
}

public void setImages(List<LookbookImage> images) {
    this.images = images;
}

    // getter setter
}