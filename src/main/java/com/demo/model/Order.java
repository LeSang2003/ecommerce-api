package com.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Double totalPrice;

    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // NEW
    private String customerName;

    private String phone;

    private String address;
    
    private String paymentMethod;

    private String transactionNo;

    private String bankCode;

    private LocalDateTime paymentTime;
    
    private String couponCode;
    
    private Double discountPercent;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private List<Review> reviews;
}