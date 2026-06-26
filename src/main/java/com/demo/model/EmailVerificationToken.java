package com.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //token random
  private String token;

  //user so huu token
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  //het han
  private LocalDateTime expiryDate;
}
