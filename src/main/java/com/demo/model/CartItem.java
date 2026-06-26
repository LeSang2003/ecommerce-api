package com.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id")
 @JsonIgnore
 private User user;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "product_id")
 @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
 private Product product;

 private Integer quantity;

}