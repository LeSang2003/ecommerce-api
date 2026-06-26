package com.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "lookbook_images")
public class LookbookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private Integer displayOrder;

    @Enumerated(EnumType.STRING)
    private LayoutType layoutType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookbook_id")
    @JsonBackReference("lookbook-images")
    private Lookbook lookbook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @JsonBackReference("section-images")
    private LookbookSection section;

    public LookbookImage() {
    }

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

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    public Lookbook getLookbook() {
        return lookbook;
    }

    public void setLookbook(Lookbook lookbook) {
        this.lookbook = lookbook;
    }

    public LookbookSection getSection() {
    return section;
}

public void setSection(LookbookSection section) {
    this.section = section;
}
}