package com.demo.repository;

import com.demo.model.Lookbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface LookbookRepository
        extends JpaRepository<Lookbook, Long> {

    Optional<Lookbook> findBySlug(String slug);

    Optional<Lookbook> findByFeaturedTrue();

    Optional<Lookbook> findFirstByOrderByYearDesc();

    @Modifying
    @Transactional
    @Query("UPDATE Lookbook l SET l.featured = false")
    void clearFeatured();

    long countByFeaturedTrue();
}