package com.demo.repository;
import com.demo.model.Product;
import com.demo.model.User;
import com.demo.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
  List<Wishlist> findByUser(User user);
  Optional<Wishlist> findByUserAndProduct(
    User user,
    Product product
  );
  boolean existsByUserAndProduct(
    User user,
    Product product
  );
  void deleteByUserAndProduct(
    User user,
    Product product
  );
  // Tổng số wishlist item
long count();

// Số user khác nhau đã dùng wishlist
@Query("""
    SELECT COUNT(DISTINCT w.user.id)
    FROM Wishlist w
""")
Long countUniqueUsers();

// Product được wishlist nhiều nhất
@Query("""
    SELECT p.name
    FROM Wishlist w
    JOIN w.product p
    GROUP BY p.id, p.name
    ORDER BY COUNT(w.id) DESC
""")
List<String> findMostWishedProduct();

// Top wishlist products
@Query("""
    SELECT new com.demo.dto.TopWishlistProductDTO(
        p.name,
        COUNT(w.id)
    )
    FROM Wishlist w
    JOIN w.product p
    GROUP BY p.id, p.name
    ORDER BY COUNT(w.id) DESC
""")
List<com.demo.dto.TopWishlistProductDTO> getTopWishlistProducts();

}
