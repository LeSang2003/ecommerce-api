package com.demo.repository;

import com.demo.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.LocalDateTime;

public interface ContactMessageRepository
        extends JpaRepository<ContactMessage, Long> {

    long countByReadStatusFalse();

    long countByCreatedAtAfter(
            LocalDateTime dateTime
    );

    Optional<ContactMessage> findByEmail(
        String email
);
}