package com.demo.repository;

import com.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  //get the user's order list  
  List<Order> findByUser(User user);
  //Pagination orders for admin
  Page<Order> findAll(Pageable pageable);

  //search orders by status
  List<Order> findByStatus(OrderStatus status);

  //dashboard statics
  @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = com.demo.model.OrderStatus.COMPLETED")
  Double getTotalRevenue();

  //revenue today
  @Query("""
  SELECT SUM(o.totalPrice)
  FROM Order o
  WHERE o.status = com.demo.model.OrderStatus.COMPLETED
  AND o.createAt BETWEEN :start AND :end
  """)
  Double getRevenueToday(LocalDateTime start, LocalDateTime end);

  //orders today
  @Query("""
  SELECT COUNT(o) 
  FROM Order o 
  WHERE o.createAt >= :start 
  AND o.createAt < :end
  """)
  Long countOrdersToday(LocalDateTime start, LocalDateTime end);

  //total users
  long count();

  //Latest Orders
  List<Order> findTop5ByOrderByCreateAtDesc();

  //Orders Statistics
  @Query("""
      SELECT o.status, COUNT(o)
      FROM Order o
      GROUP BY o.status
      """)
  List<Object[]> countOrdersByStatus();

  //Top Customers buying
  @Query("""
      SELECT o.user.id, o.user.email, SUM(o.totalPrice)
      FROM Order o
      WHERE o.status = com.demo.model.OrderStatus.COMPLETED
      GROUP BY o.user.id, o.user.email
      ORDER BY SUM(o.totalPrice) DESC 
    """)
  List<Object[]> getTopCustomers(Pageable pageable);
  
  //Reveue last 7 days
  @Query(value = """
SELECT
    DATE(o.create_at) as revenue_date,
    SUM(o.total_price) as revenue
FROM orders o
WHERE o.status = 'COMPLETED'
AND DATE(o.create_at) >= CURRENT_DATE - INTERVAL '6 days'
GROUP BY DATE(o.create_at)
ORDER BY revenue_date
""", nativeQuery = true)
List<Object[]> getRevenueLast7Days();
  
  //Revenue 30days
 @Query(value = """
SELECT
    DATE(o.create_at) as revenue_date,
    SUM(o.total_price) as revenue
FROM orders o
WHERE o.status = 'COMPLETED'
AND DATE(o.create_at) >= CURRENT_DATE - INTERVAL '29 days'
GROUP BY DATE(o.create_at)
ORDER BY revenue_date
""", nativeQuery = true)
List<Object[]> getRevenueLast30Days();
  //RevenueByMonth
  @Query(value = """
SELECT
    TO_CHAR(o.create_at, 'YYYY-MM') as month,
    SUM(o.total_price) as revenue
FROM orders o
WHERE o.status='COMPLETED'
GROUP BY TO_CHAR(o.create_at, 'YYYY-MM')
ORDER BY month
""", nativeQuery = true)
List<Object[]> getRevenueByMonth();

  @Query("""
  SELECT o FROM Order o
  LEFT JOIN FETCH o.items i
  LEFT JOIN FETCH i.product
  WHERE o.id = :id
  """)
  Optional<Order> findByIdWithItems(@Param("id") Long id);


 @Query(value = """
SELECT
    DATE(create_at) as revenue_date,
    SUM(total_price) as total
FROM orders
WHERE status='COMPLETED'
GROUP BY DATE(create_at)
ORDER BY revenue_date
""", nativeQuery = true)
List<Object[]> revenueByDaysRaw();


// USER PAGINATION ORDERS
Page<Order> findByUser(User user, Pageable pageable);

Page<Order> findByStatus(OrderStatus status, Pageable pageable);

Page<Order> findByPaymentMethod(String paymentMethod, Pageable pageable);

Page<Order> findByStatusAndPaymentMethod(
    OrderStatus status,
    String paymentMethod,
    Pageable pageable
);
} 
