package com.demo.controller;

import com.demo.dto.DashboardResponse;
import com.demo.dto.OrderItemResponse;
import com.demo.dto.OrderRequest;
import com.demo.dto.OrderResponse;
import com.demo.dto.OrderStatusStatsResponse;
import com.demo.dto.RevenueByMonthResponse;
import com.demo.dto.RevenueLast7DaysResponse;
import com.demo.dto.RevenueResponse;
import com.demo.dto.TopCustomerResponse;
import com.demo.dto.TopProductResponse;
import com.demo.model.User;
import com.demo.service.OrderService;
import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.model.OrderStatus;
import com.demo.model.Role;
import com.demo.repository.OrderRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.security.core.Authentication;
//pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final UserRepository userRepository;
  private final OrderRepository orderRepository;

  
  // checkout
  @PostMapping("/checkout")
  public OrderResponse checkout(Authentication authentication){

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return orderService.checkout(user);
  }

  // USER PAGINATION ORDERS
  @GetMapping("/my-orders")
  public Page<OrderResponse> getMyOrders(
        Authentication authentication,
        Pageable pageable
  ) {

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    return orderService.getMyOrdersPagination(
            user,
            pageable
    ).map(order -> {

        OrderResponse response =
                new OrderResponse();

        response.setOrderId(order.getId());

        response.setTotalPrice(
                order.getTotalPrice()
        );

        response.setStatus(
                order.getStatus().name()
        );
        response.setCreateAt(
            order.getCreateAt()
        );
         // PAYMENT
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTransactionNo(order.getTransactionNo());
        response.setBankCode(order.getBankCode());
        response.setPaymentTime(order.getPaymentTime());

        // COUPON
        response.setCouponCode(order.getCouponCode());
        response.setDiscountPercent(order.getDiscountPercent());
        List<OrderItemResponse> items =
                order.getItems().stream().map(item -> {

                    OrderItemResponse res =
                            new OrderItemResponse();

                    res.setProductId(
                            item.getProduct().getId()
                    );

                    res.setProductName(
                            item.getProduct().getName()
                    );

                    res.setQuantity(
                            item.getQuantity()
                    );

                    res.setPrice(
                            item.getPrice()
                    );

                    res.setImageUrl(
                            item.getProduct().getImageUrl()
                    );

                    res.setColor(
                            item.getColor()
                    );

                    res.setSize(
                            item.getSize()
                    );

                    return res;

                }).toList();

        response.setItems(items);

        return response;
    });
  }

  // get order detail
  @GetMapping("/{orderId}/items")
  public List<OrderItem> getOrderDetail(@PathVariable Long orderId){
    return orderService.getOrderDetail(orderId);
  }

  // admin update status
  @PutMapping("/{id}/status")
  public Order updateStatus(
          @PathVariable Long id,
          @RequestParam OrderStatus status){
    return orderService.updateStatus(id, status);
  }

  //admin get all orders
  @GetMapping("/admin/all")
  public List<OrderResponse> getAllOrders() {
    return orderService.getAllOrdersDTO();
  }

  //admin revenue statistics
  @GetMapping("/admin/revenue")
  public RevenueResponse getRevenue(){
    return orderService.getRevenue();
  }

  //user cancel order
  @PutMapping("/{id}/cancel")
  public Order cancelOrder(@PathVariable Long id, Authentication authentication){

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return orderService.cancelOrder(id,user);
  }

  //Pagination
  @GetMapping("/admin/orders")
  public Page <Order> getOrders(Pageable pageable){
    return orderService.getOrdersWithPagination(pageable);
  }
  @GetMapping("/admin/orders/filter")
  public Page<Order> filterOrders(
    @RequestParam(required = false) OrderStatus status,
    @RequestParam(required = false) String paymentMethod,
    Pageable pageable
  ) {
    if (status != null && paymentMethod != null && !paymentMethod.isBlank()) {
        return orderRepository.findByStatusAndPaymentMethod(
            status,
            paymentMethod,
            pageable
        );
    }

    if (status != null) {
        return orderRepository.findByStatus(status, pageable);
    }

    if (paymentMethod != null && !paymentMethod.isBlank()) {
        return orderRepository.findByPaymentMethod(
            paymentMethod,
            pageable
        );
    }

    return orderRepository.findAll(pageable);
  }

  //Top Selling Product
  @GetMapping("/admin/top-products")
  public List<TopProductResponse> getTopProducts(){
    return orderService.getTopProducts();
  }
  
  @GetMapping("/admin/revenue-30-days")
  public List<RevenueLast7DaysResponse> getRevenueLast30Days(){
    return orderService.getRevenueLast30Days();
  }

  //search orders by status
  @GetMapping("/admin/search")
  public List<Order> searchOrdersByStatus(@RequestParam OrderStatus status){
    return orderService.searchOrdersByStatus(status);
  }

  //dashboard statics
  @GetMapping("/admin/dashboard")
  public DashboardResponse getDashboardStats(){
    return orderService.getDashboardStats();
  }

  //revenue today
  @GetMapping("/admin/revenue/today")
  public Double revenueToday(){
    return orderService.getRevenueToday();
  }

  //orders today
  @GetMapping("/admin/orders/today")
  public Long getOrdersToday(){
    return orderService.getOrdersToday();
  }

  //total users
  @GetMapping("/admin/users/total")
  public Long getTotalUsers(){
    return orderService.getTotalUsers();
  }

  //latest orders
  @GetMapping("/admin/orders/latest")
  public List<Order> getLatestOrders(){
    return orderService.getLatestOrders();
  }

  //Orders Statistics
  @GetMapping("/admin/orders/statistics")
  public List<OrderStatusStatsResponse> getOrderStatistics(){
    return orderService.getOrderStatusStats();
  }

  //Top Customers Buying
  @GetMapping("/admin/customers/top")
  public List<TopCustomerResponse> getTopCustomers(){
    return orderService.getTopCustomers();
  }

  //Revenue last 7 days
  @GetMapping("/admin/revenue-7-days")
  public List<RevenueLast7DaysResponse> getRevenueLast7Days(){
    return orderService.getRevenueLast7Days();
  }

  @GetMapping("/{id}")
  public OrderResponse getOrder(@PathVariable Long id) {

    Order order = orderRepository.findByIdWithItems(id)
        .orElseThrow(() -> new RuntimeException("Order not found"));

    OrderResponse res = new OrderResponse();
    res.setOrderId(order.getId());
    res.setTotalPrice(order.getTotalPrice());
    res.setStatus(order.getStatus().name());
     // FIX DATE
    res.setCreateAt(order.getCreateAt());

    // FIX PAYMENT
    res.setPaymentMethod(order.getPaymentMethod());
    res.setTransactionNo(order.getTransactionNo());
    res.setBankCode(order.getBankCode());
    res.setPaymentTime(order.getPaymentTime());

    // FIX COUPON
    res.setCouponCode(order.getCouponCode());

    res.setDiscountPercent(order.getDiscountPercent());
    // MAP ITEMS
   List<OrderItemResponse> itemResponses =
    order.getItems() != null
        ? order.getItems().stream().map(item -> {
            OrderItemResponse dto = new OrderItemResponse();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setImageUrl(item.getProduct().getImageUrl());
            dto.setSize(item.getSize());
            dto.setColor(item.getColor());
            return dto;
        }).toList()
        : List.of(); // tránh null

    res.setItems(itemResponses);

    return res;
  }

  @GetMapping("/admin/revenue-by-month")
  public List<RevenueByMonthResponse> revenueByMonth(){
    System.out.println(">>> CALL MONTH API");
    return orderService.getRevenueByMonth();
  }
  //Them dropdown change to role
  @PutMapping("/admin/users/{id}/role")
  public User updateRole(@PathVariable Long id, @RequestParam String role){
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setRole(Role.valueOf(role));
    return userRepository.save(user);
  }
  //toggle ban/unban user
  @PutMapping("/admin/users/{id}/toggle-ban")
  public User toggleBan(@PathVariable Long id, Authentication authentication){

    // user hiện tại
    String currentUsername = authentication.getName();

    User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new RuntimeException("Current user not found"));

    // không cho tự ban/unban chính mình
    if (currentUser.getId().equals(id)) {
        throw new RuntimeException("You cannot modify yourself");
    }

    // user target
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // toggle
    user.setBanned(!Boolean.TRUE.equals(user.getBanned()));

    return userRepository.save(user);
  }
  //Pagination user
  @GetMapping("/admin/users")
  public Page<User> getUsers(Pageable pageable){
    return userRepository.findAll(pageable);
  }

  @PostMapping
  public OrderResponse createOrder(
        @RequestBody OrderRequest request,
        Authentication authentication
  ) {
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return orderService.createOrder(user, request);
  }
  
  //Admin detail modal
  @GetMapping("/admin/orders/{id}")
  public ResponseEntity<?> getOrderDetailAdmin(@PathVariable Long id){
    return ResponseEntity.ok(orderService.getOrderDetailDTO(id));
  }

  @GetMapping("/admin/orders/export/excel")
public ResponseEntity<byte[]> exportExcel() throws Exception {
    byte[] data = orderService.exportOrdersToExcel();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=orders.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(data);
}

@GetMapping("/admin/orders/export/pdf")
public ResponseEntity<byte[]> exportPdf() throws Exception {
    byte[] data = orderService.exportOrdersToPdf();

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=orders.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(data);
}
}
