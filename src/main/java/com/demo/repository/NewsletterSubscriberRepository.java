package com.demo.repository;

import com.demo.model.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NewsletterSubscriberRepository
        extends JpaRepository<NewsletterSubscriber, Long> {

    Optional<NewsletterSubscriber> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActiveTrue();

    long countBySubscribedAtAfter(
            LocalDateTime dateTime
    );
}