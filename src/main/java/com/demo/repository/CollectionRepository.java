package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.model.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {

    Collection findBySlug(String slug);

    Collection findFirstByFeaturedTrue();

    long countByFeaturedTrue();

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.collection IS NOT NULL
    """)
    Long countProductsInCollections();

    @Query("""
    SELECT c.name
    FROM Product p
    JOIN p.collection c
    GROUP BY c.id, c.name
    ORDER BY COUNT(p.id) DESC
    """)
    List<String> findLargestCollections();
}