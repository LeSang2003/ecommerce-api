package com.demo.service;

import com.demo.dto.lookbook.CreateLookbookRequest;
import com.demo.dto.lookbook.LookbookResponse;
import java.util.List;
import com.demo.dto.lookbook.LookbookStatsResponse;
public interface LookbookService {

    List<LookbookResponse> getAll();

    LookbookResponse getBySlug(String slug);

    LookbookResponse getFeatured();

    LookbookResponse create(
        CreateLookbookRequest request
    );

    LookbookResponse update(
        Long id,
        CreateLookbookRequest request
    );

    void delete(Long id);

    LookbookStatsResponse getStats();
}