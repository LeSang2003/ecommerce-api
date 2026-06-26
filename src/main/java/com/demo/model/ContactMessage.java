package com.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt;

    private Boolean readStatus = false;

     // NEW

    private Boolean replied = false;

    @Column(columnDefinition = "TEXT")
    private String replyContent;

    private LocalDateTime repliedAt;

    private String repliedBy;
}