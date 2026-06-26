package com.demo.service;


import com.demo.dto.CouponValidationResponse;
import com.demo.dto.DashboardResponse;
import com.demo.dto.OrderDetailResponse;
import com.demo.dto.OrderItemRequest;
import com.demo.dto.OrderItemResponse;
import com.demo.dto.OrderRequest;
import com.demo.dto.OrderResponse;
import com.demo.dto.OrderStatusStatsResponse;
import com.demo.dto.RevenueByDay;
import com.demo.dto.RevenueByMonthResponse;
import com.demo.dto.RevenueLast7DaysResponse;
import com.demo.dto.RevenueResponse;
import com.demo.dto.TopCustomerResponse;
import com.demo.dto.TopProductResponse;
//coupon
import com.demo.model.*;
//dashboard stics
import com.demo.repository.*;
//import com.stripe.model.Coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.demo.dto.CouponStatsResponse;
//import org.springframework.cglib.core.Local;
//pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DiscountCouponRepository discountCouponRepository;
    private final MailService mailService;
    @Transactional
    public OrderResponse checkout(User user) {

    List<CartItem> cartItems = cartRepository.findByUser(user);

    if (cartItems.isEmpty()) {
        throw new RuntimeException("Cart is empty");
    }

    double totalPrice = cartItems.stream()
            .mapToDouble(item ->
                    item.getProduct().getPrice() * item.getQuantity())
            .sum();

    Order order = new Order();
    order.setUser(user);
    order.setTotalPrice(totalPrice);
    order.setCreateAt(LocalDateTime.now());
    order.setStatus(OrderStatus.PENDING);

    //tạo list OrderItem
    List<OrderItem> orderItems = cartItems.stream().map(item -> {
        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setProduct(item.getProduct());
        oi.setQuantity(item.getQuantity());
        oi.setPrice(item.getProduct().getPrice());
        return oi;
    }).toList();

    //gán vào order
    order.setItems(orderItems);

    // chỉ save 1 lần (cascade)
    orderRepository.save(order);

    //map sang response
    List<OrderItemResponse> items = orderItems.stream().map(item -> {
        OrderItemResponse res = new OrderItemResponse();
        res.setProductId(item.getProduct().getId());
        res.setProductName(item.getProduct().getName());
        res.setQuantity(item.getQuantity());
        res.setPrice(item.getPrice());
        res.setImageUrl(item.getProduct().getImageUrl()); // thêm ảnh luôn
        return res;
    }).toList();

    cartRepository.deleteAll(cartItems);

    OrderResponse response = new OrderResponse();
    response.setOrderId(order.getId());
    response.setTotalPrice(totalPrice);
    response.setStatus(order.getStatus().name());
    response.setCreateAt(order.getCreateAt());
    response.setItems(items);

    return response;
    }
    //get the user's order list
    public List<Order> getMyOrders(User user){
        return orderRepository.findByUser(user);
    }
    
    //get the orderDetail
   public List<OrderItem> getOrderDetail(Long orderId){

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    return orderItemRepository.findByOrder(order);
    }

    //Admin update orderstatus
    public Order updateStatus(
        Long orderId,
        OrderStatus status
) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() ->
                    new RuntimeException("Order not found"));

    order.setStatus(status);

    Order savedOrder =
            orderRepository.save(order);

    // Send email
    if (savedOrder.getUser() != null) {

        mailService.sendOrderStatusEmail(
                savedOrder.getUser().getEmail(),
                savedOrder.getId(),
                savedOrder.getStatus()
        );
    }

    return savedOrder;
}

    //Admin get all orders
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    //admin revenue statistics
    public RevenueResponse getRevenue(){
        List<Order> orders = orderRepository.findAll();
        long totalOrders = orders.size();
        double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        RevenueResponse res = new RevenueResponse();
        res.setTotalOrders(totalOrders);
        res.setTotalRevenue(totalRevenue);
        return res;
 
    }

    //User cancel order
    public Order cancelOrder(Long orderId, User user){

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

    // CHECK OWNER
    if(!order.getUser().getId().equals(user.getId())){
        throw new RuntimeException("You cannot cancel this order");
    }

    // ONLY PENDING
    if(order.getStatus() != OrderStatus.PENDING){
        throw new RuntimeException("Only PENDING orders can be cancelled");
    }

    // =========================
    // RESTORE STOCK
    // =========================

    for (OrderItem item : order.getItems()) {

        Product product = item.getProduct();

        product.setStock(
                product.getStock() + item.getQuantity()
        );

        productRepository.save(product);
    }

    // CANCEL ORDER
    order.setStatus(OrderStatus.CANCELLED);

    return orderRepository.save(order);
    }

    //Pagination
    public Page<Order> getOrdersWithPagination(Pageable pageable){
        return orderRepository.findAll(pageable);
    }

    //Top Selling Product
    public List<TopProductResponse> getTopProducts(){
        return orderItemRepository.getTopProducts();
    }

    //Revenue By Month
    public List<RevenueByMonthResponse> getRevenueByMonth(){

    List<Object[]> results = orderRepository.getRevenueByMonth();
    System.out.println("CALL MONTH API");   
    return results.stream().map(r -> {

        RevenueByMonthResponse res = new RevenueByMonthResponse();

        res.setMonth(String.valueOf(r[0]));
        res.setRevenue(((Number) r[1]).doubleValue());

        return res;

    }).toList();
    }

    //search orders by status
    public List<Order> searchOrdersByStatus(OrderStatus status){
        return orderRepository.findByStatus(status);
    }
    
    //dashboard statistics
    public DashboardResponse getDashboardStats(){

    long totalUsers = userRepository.count();
    long totalOrders = orderRepository.count();
    long totalProducts = productRepository.count();
    Double totalRevenue = orderRepository.getTotalRevenue();

    DashboardResponse res = new DashboardResponse();

    res.setTotalUsers(totalUsers);
    res.setTotalOrders(totalOrders);
    res.setTotalProducts(totalProducts);
    res.setTotalRevenue(totalRevenue != null ? totalRevenue : 0);

    
    List<Object[]> raw = orderRepository.revenueByDaysRaw();

    List<RevenueByDay> revenueList = raw.stream()
        .map(r -> new RevenueByDay(
            r[0].toString(),                    // date
            ((Number) r[1]).doubleValue()       // total
        ))
        .toList();

    res.setRevenueByDays(revenueList);

    return res;
}

    //revenue today
    public Double getRevenueToday(){
    LocalDateTime start = LocalDate.now().atStartOfDay();
    LocalDateTime end = start.plusDays(1);

    Double revenueToday = orderRepository.getRevenueToday(start, end);
    return revenueToday != null ? revenueToday : 0.0;
    }

    //orders today
    public Long getOrdersToday(){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return orderRepository.countOrdersToday(start,end);  
    }

    //total users
    public Long getTotalUsers(){
        return userRepository.count();
    }

    //latest orders
    public List<Order> getLatestOrders(){
        return orderRepository.findTop5ByOrderByCreateAtDesc();
    }

    //Orders Statistics
    public List<OrderStatusStatsResponse> getOrderStatusStats(){
        List<Object[]> results = orderRepository.countOrdersByStatus();
        return results.stream().map(r -> {
            OrderStatusStatsResponse res = new OrderStatusStatsResponse();
            res.setStatus((OrderStatus) r[0]);
            res.setTotal(((Number) r[1]).longValue());
            return res;
        }).toList();
    }

    //Top Customers Buying
    public List<TopCustomerResponse> getTopCustomers(){
    Pageable pageable = Pageable.ofSize(5);
    List<Object[]> results = orderRepository.getTopCustomers(pageable);
    return results.stream().map(r -> {

        TopCustomerResponse res = new TopCustomerResponse();

        res.setUserId((Long) r[0]);
        res.setEmail((String) r[1]);
        res.setTotalSpent(((Number) r[2]).doubleValue());
        return res;
    }).toList();
    }

    //Revenue last 7 days
   public List<RevenueLast7DaysResponse> getRevenueLast7Days() {

    List<Object[]> results = orderRepository.getRevenueLast7Days();

    Map<String, Double> revenueMap = new HashMap<>();

    // Map data từ DB
    for (Object[] r : results) {
        String date = r[0].toString();
        Double revenue = ((Number) r[1]).doubleValue();
        revenueMap.put(date, revenue);
    }

    List<RevenueLast7DaysResponse> response = new ArrayList<>();

    // Tạo đủ 7 ngày
    for (int i = 6; i >= 0; i--) {
        LocalDate date = LocalDate.now().minusDays(i);
        String dateStr = date.toString();

        RevenueLast7DaysResponse res = new RevenueLast7DaysResponse();
        res.setDate(dateStr);
        res.setRevenue(revenueMap.getOrDefault(dateStr, 0.0));

        response.add(res);
    }

    return response;
    }

    public List<OrderResponse> getAllOrdersDTO() {
    List<Order> orders = orderRepository.findAll();

    return orders.stream().map(order -> {
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(order.getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus().name());
        dto.setCreateAt(order.getCreateAt());

        List<OrderItemResponse> items = order.getItems().stream().map(item -> {
            OrderItemResponse i = new OrderItemResponse();
            i.setProductId(item.getProduct().getId());
            i.setProductName(item.getProduct().getName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            return i;
        }).toList();

        dto.setItems(items);
        return dto;
    }).toList();
    }

    public List<RevenueLast7DaysResponse> getRevenueLast30Days() {

    List<Object[]> results = orderRepository.getRevenueLast30Days();
    for (Object[] r : results) {
        System.out.println("DATE: " + r[0] + " | REV: " + r[1]);
    }
    Map<String, Double> revenueMap = new HashMap<>();

    for (Object[] r : results) {
        String date = r[0].toString();
        Double revenue = ((Number) r[1]).doubleValue();
        revenueMap.put(date, revenue);
    }

    List<RevenueLast7DaysResponse> response = new ArrayList<>();

    for (int i = 29; i >= 0; i--) {
        LocalDate date = LocalDate.now().minusDays(i);
        String dateStr = date.toString();

        RevenueLast7DaysResponse res = new RevenueLast7DaysResponse();
        res.setDate(dateStr);
        res.setRevenue(revenueMap.getOrDefault(dateStr, 0.0));

        response.add(res);
    }

    return response;
    }

  
    @Transactional
    public OrderResponse createOrder(User user, OrderRequest request) {

    double totalPrice = 0;

    Order order = new Order();

    order.setUser(user);

    order.setCreateAt(LocalDateTime.now());

    order.setStatus(OrderStatus.PENDING);

    // CUSTOMER INFO
    order.setCustomerName(request.getCustomerName());

    order.setPhone(request.getPhone());

    order.setAddress(request.getAddress());

    // PAYMENT
    order.setPaymentMethod(request.getPaymentMethod());

    List<OrderItem> orderItems = new ArrayList<>();

    // =========================
    // ITEMS
    // =========================

    for (OrderItemRequest itemReq : request.getItems()) {

        Product product = productRepository.findById(
                itemReq.getProductId()
        ).orElseThrow(() ->
                new RuntimeException("Product not found"));

        // CHECK STOCK
        if (product.getStock() < itemReq.getQuantity()) {

            throw new RuntimeException(
                    product.getName() + " is out of stock"
            );
        }

        // MINUS STOCK
        product.setStock(
                product.getStock() - itemReq.getQuantity()
        );

        productRepository.save(product);

        OrderItem item = new OrderItem();

        item.setOrder(order);

        item.setProduct(product);

        item.setQuantity(itemReq.getQuantity());

        item.setPrice(product.getPrice());

        item.setColor(itemReq.getColor());

        item.setSize(itemReq.getSize());

        totalPrice +=
                product.getPrice() * itemReq.getQuantity();

        orderItems.add(item);
    }

    // =========================
    // APPLY COUPON
    // =========================

    if (
            request.getCouponCode() != null
            && !request.getCouponCode().isBlank()
    ) {

        DiscountCoupon coupon =
                discountCouponRepository.findByCode(
                        request.getCouponCode()
                ).orElseThrow(() ->
                        new RuntimeException("Coupon not found"));

        // CHECK ACTIVE
        if (!coupon.getActive()) {

            throw new RuntimeException(
                    "Coupon inactive"
            );
        }

        // CHECK EXPIRED
        if (
                coupon.getExpiredAt()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "Coupon expired"
            );
        }
         // DEBUG
        System.out.println(
                "TOTAL BEFORE: " + totalPrice
        );

        System.out.println(
                "DISCOUNT %: " +
                coupon.getDiscountPercent()
        );
        // CALCULATE DISCOUNT
        double discount =
                totalPrice *
                coupon.getDiscountPercent() / 100;
        System.out.println(
                "DISCOUNT MONEY: " + discount
        );
        totalPrice -= discount;

        System.out.println(
                "TOTAL AFTER: " + totalPrice
        );
        // SAVE ORDER INFO
        order.setCouponCode(
                coupon.getCode()
        );

        order.setDiscountPercent(
                coupon.getDiscountPercent()
        );
        // TĂNG LƯỢT DÙNG COUPON
        coupon.setUsedCount(
                coupon.getUsedCount() + 1
        );
        if (coupon.getUsedCount() >= coupon.getMaxUsage()) {
                coupon.setActive(false);
        }
        discountCouponRepository.save(coupon);
    }

    // =========================
    // SAVE ORDER
    // =========================

    order.setItems(orderItems);

    order.setTotalPrice(totalPrice);

    orderRepository.save(order);

    // =========================
    // RESPONSE
    // =========================

    OrderResponse response = new OrderResponse();

    response.setOrderId(order.getId());

    response.setTotalPrice(totalPrice);

    response.setStatus(order.getStatus().name());
    
    response.setCreateAt(order.getCreateAt());

    response.setPaymentMethod(
            order.getPaymentMethod()
    );

    response.setCouponCode(
            order.getCouponCode()
    );

    response.setDiscountPercent(
            order.getDiscountPercent()
    );

    List<OrderItemResponse> items =
            orderItems.stream().map(item -> {

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
        // =========================
        // CLEAR CART AFTER ORDER SUCCESS
        // =========================
        List<CartItem> cartItems = cartRepository.findByUser(user);

        cartRepository.deleteAll(cartItems);

        return response;
}

//Admin detail modal
    public OrderDetailResponse getOrderDetailDTO(Long orderId) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() ->
                    new RuntimeException("Order not found"));

    OrderDetailResponse response =
            new OrderDetailResponse();

    response.setOrderId(order.getId());

    response.setCustomerName(order.getCustomerName());

    response.setPhone(order.getPhone());

    response.setAddress(order.getAddress());

    response.setPaymentMethod(
            order.getPaymentMethod()
    );

    response.setCouponCode(
            order.getCouponCode()
    );

    response.setDiscountPercent(
            order.getDiscountPercent()
    );

    response.setTotalPrice(
            order.getTotalPrice()
    );

    response.setStatus(
            order.getStatus().name()
    );

    response.setCreatedAt(
            order.getCreateAt()
    );

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

                res.setImageUrl(
                        item.getProduct().getImageUrl()
                );

                res.setQuantity(
                        item.getQuantity()
                );

                res.setPrice(
                        item.getPrice()
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
}
// USER PAGINATION ORDERS
public Page<Order> getMyOrdersPagination(
        User user,
        Pageable pageable
) {
    return orderRepository.findByUser(user, pageable);
}
  public CouponValidationResponse validateCoupon(
        String code,
        Double totalPrice
) {
    DiscountCoupon coupon = discountCouponRepository
            .findByCode(code)
            .orElse(null);

    if (coupon == null) {
        return new CouponValidationResponse(
                false,
                "Coupon not found",
                null
        );
    }

    if (!coupon.getActive()) {
        return new CouponValidationResponse(
                false,
                "Coupon inactive",
                null
        );
    }

    if (coupon.getExpiredAt() != null &&
            coupon.getExpiredAt().isBefore(LocalDateTime.now())) {

        coupon.setActive(false);
        discountCouponRepository.save(coupon);

        return new CouponValidationResponse(
                false,
                "Coupon expired",
                null
        );
    }

    // đơn tối thiểu
    if (coupon.getMinOrderValue() != null &&
            totalPrice < coupon.getMinOrderValue()) {

        return new CouponValidationResponse(
                false,
                "Minimum order is " + coupon.getMinOrderValue(),
                null
        );
    }

    // hết lượt
    if (coupon.getMaxUsage() != null &&
            coupon.getUsedCount() >= coupon.getMaxUsage()) {

        return new CouponValidationResponse(
                false,
                "Coupon usage limit reached",
                null
        );
    }

    return new CouponValidationResponse(
            true,
            "Coupon applied successfully",
            coupon.getDiscountPercent()
    );
}
public CouponStatsResponse getCouponStats() {

    long totalCoupons = discountCouponRepository.count();

    long activeCoupons =
            discountCouponRepository.countByActiveTrue();

    long usedCoupons =
            discountCouponRepository
                    .countByUsedCountGreaterThan(0);

    String mostUsedCoupon =
            discountCouponRepository.findAll()
                    .stream()
                    .max((a, b) ->
                            Integer.compare(
                                    a.getUsedCount() == null ? 0 : a.getUsedCount(),
                                    b.getUsedCount() == null ? 0 : b.getUsedCount()
                            ))
                    .map(DiscountCoupon::getCode)
                    .orElse("N/A");

    return new CouponStatsResponse(
            totalCoupons,
            activeCoupons,
            usedCoupons,
            mostUsedCoupon
    );
}
public byte[] exportOrdersToExcel() throws Exception {
    List<Order> orders = orderRepository.findAll();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Orders");

    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("ID");
    header.createCell(1).setCellValue("Customer");
    header.createCell(2).setCellValue("Total");
    header.createCell(3).setCellValue("Status");

    int rowNum = 1;

    for (Order order : orders) {
        Row row = sheet.createRow(rowNum++);
        String customer = order.getUser() != null
            ? order.getUser().getEmail()
            : order.getCustomerName();
        row.createCell(0).setCellValue(order.getId());
        row.createCell(1).setCellValue(customer);
        row.createCell(2).setCellValue(order.getTotalPrice());
        row.createCell(3).setCellValue(order.getStatus().toString());
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();

    return out.toByteArray();
}

public byte[] exportOrdersToPdf() throws Exception {
    List<Order> orders = orderRepository.findAll();

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    Document document = new Document();
    PdfWriter.getInstance(document, out);

    document.open();

    document.add(new Paragraph("Orders Report"));

    PdfPTable table = new PdfPTable(4);

    table.addCell("ID");
    table.addCell("Customer");
    table.addCell("Total");
    table.addCell("Status");

        for (Order order : orders) {

        String customer = order.getUser() != null
            ? order.getUser().getEmail()
            : order.getCustomerName();

        table.addCell(String.valueOf(order.getId()));
        table.addCell(customer);
        table.addCell(String.valueOf(order.getTotalPrice()));
        table.addCell(order.getStatus().toString());
        }

    document.add(table);
    document.close();

    return out.toByteArray();
}
}

    
   
