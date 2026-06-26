package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.demo.dto.CollectionPerformanceDTO;
import com.demo.model.OrderItem;
import com.demo.dto.TopProductResponse;
import com.demo.model.Order;
import com.demo.dto.TopCollectionDTO;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  //Look at the order's detail
  List<OrderItem> findByOrder(Order order);

  //Top Selling product
  @Query("""
  SELECT new com.demo.dto.TopProductResponse(
    p.name,
    SUM(oi.quantity)
  )
  FROM OrderItem oi
  JOIN oi.product p
  GROUP BY p.name
  ORDER BY SUM(oi.quantity) DESC
  """)
  List<TopProductResponse> getTopProducts();

   @Query("""
        SELECT COALESCE(SUM(oi.quantity),0)
        FROM OrderItem oi
        WHERE oi.product.id = :productId
    """)
    Long getSoldByProduct(Long productId);

  @Query("""
  SELECT new com.demo.dto.CollectionPerformanceDTO(
    c.name,
    SUM(oi.quantity),
    SUM(oi.quantity * oi.price)
  )
    FROM OrderItem oi
    JOIN oi.product p
    JOIN p.collection c
    JOIN oi.order o
    WHERE o.status = com.demo.model.OrderStatus.COMPLETED
    AND c IS NOT NULL
    GROUP BY c.name
    ORDER BY SUM(oi.quantity) DESC
    """)
    List<CollectionPerformanceDTO> getCollectionPerformance();


    @Query("""
  SELECT new com.demo.dto.TopCollectionDTO(
    c.name,
    COUNT(DISTINCT p.id),
    SUM(oi.quantity),
    SUM(oi.quantity * oi.price)
  )
    FROM OrderItem oi
    JOIN oi.product p
    JOIN p.collection c
      JOIN oi.order o
    WHERE o.status = com.demo.model.OrderStatus.COMPLETED
    AND c IS NOT NULL
    GROUP BY c.id, c.name
    ORDER BY SUM(oi.quantity * oi.price) DESC
    """)
    List<TopCollectionDTO> getTopCollections(); 
}
