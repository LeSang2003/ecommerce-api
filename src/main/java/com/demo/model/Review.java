package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //1->5 stars
  private Integer rating;

  @Column(columnDefinition = "TEXT")
  private String comment;

  private LocalDateTime createdAt;

  //User
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  //Product
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  //Order
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  private String imageUrl;
}
