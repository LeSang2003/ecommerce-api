package com.demo.repository;

import com.demo.model.Product;
import com.demo.model.Review;
import com.demo.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {


     Optional<Review> findById(Long id);
    // reviews của product
    List<Review> findByProductOrderByCreatedAtDesc(
            Product product
    );

    // user đã review chưa
    Optional<Review> findByUserAndProduct(
            User user,
            Product product
    );

    // average rating
    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.product.id = :productId
    """)
    Double getAverageRating(Long productId);
}