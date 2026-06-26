package com.demo.controller;

import com.demo.dto.lookbook.LookbookResponse;
import com.demo.service.LookbookService;
import org.springframework.web.bind.annotation.*;
import com.demo.dto.lookbook.CreateLookbookRequest;
import java.util.List;
import com.demo.dto.lookbook.LookbookStatsResponse;
@RestController
@RequestMapping("/api/lookbooks")
@CrossOrigin
public class LookbookController {

    private final LookbookService lookbookService;

    public LookbookController(
            LookbookService lookbookService
    ) {
        this.lookbookService = lookbookService;
    }

    @GetMapping
    public List<LookbookResponse> getAll() {
        return lookbookService.getAll();
    }

    @GetMapping("/featured")
    public LookbookResponse getFeatured() {
        return lookbookService.getFeatured();
    }

    @GetMapping("/{slug}")
    public LookbookResponse getBySlug(
            @PathVariable String slug
    ) {
        return lookbookService.getBySlug(slug);
    }

    @PostMapping
    public LookbookResponse create(
        @RequestBody CreateLookbookRequest request
    ) {
        return lookbookService.create(request);
    }

    @PutMapping("/{id}")
    public LookbookResponse update(
        @PathVariable Long id,
        @RequestBody CreateLookbookRequest request
    ) {
        return lookbookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
        @PathVariable Long id
    ) {
        lookbookService.delete(id);
    }

    @GetMapping("/stats")
    public LookbookStatsResponse stats() {

        return lookbookService.getStats();
    }
}