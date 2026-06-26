package com.demo.model;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "Wishlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //User
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  //Product
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
}
