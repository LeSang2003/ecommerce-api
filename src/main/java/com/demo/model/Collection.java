package com.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Spring Summer 26
    @Column(nullable = false)
    private String name;

    // spring-summer-26
    @Column(nullable = false, unique = true)
    private String slug;

    // Banner full màn hình
    private String bannerImage;

    @Column(length = 3000)
    private String description;

    // SS / FW / LE
    @Column(length = 10)
    private String season;

    // 2026
    private Integer year;

    // Collection nổi bật trên Home
    @Column(nullable = false)
    private Boolean featured = false;

    public Collection() {
    }

    public Collection(
            String name,
            String slug,
            String bannerImage,
            String description,
            String season,
            Integer year,
            Boolean featured
    ) {
        this.name = name;
        this.slug = slug;
        this.bannerImage = bannerImage;
        this.description = description;
        this.season = season;
        this.year = year;
        this.featured = featured;
    }

    // =========================
    // GETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public String getDescription() {
        return description;
    }

    public String getSeason() {
        return season;
    }

    public Integer getYear() {
        return year;
    }

    public Boolean getFeatured() {
        return featured;
    }

    // =========================
    // SETTERS
    // =========================

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }
}