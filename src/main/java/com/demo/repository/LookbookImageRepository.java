package com.demo.repository;

import com.demo.model.LookbookImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LookbookImageRepository
        extends JpaRepository<LookbookImage, Long> {
                long count();
 }