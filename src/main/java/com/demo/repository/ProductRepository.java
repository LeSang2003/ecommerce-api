package com.demo.repository;

import com.demo.model.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
  List<Product> findByStockLessThanOrderByStockAsc(int threshold);
  Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
  List<Product> findByCategoryIdAndIdNot(Long categoryId, Long id, Pageable pageable);
  List<Product> findByCollectionSlug(String slug);
  @Query("""
    SELECT DISTINCT p.category
    FROM Product p
    WHERE UPPER(p.gender) = UPPER(:gender)
      AND p.category IS NOT NULL
""")
List<Category> findCategoriesByGender(
    @Param("gender") String gender
);
}