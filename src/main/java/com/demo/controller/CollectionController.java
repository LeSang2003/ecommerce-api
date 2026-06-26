package com.demo.controller;

import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.demo.dto.CollectionStatsDTO;
import com.demo.model.Collection;
import com.demo.service.CollectionService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import com.demo.dto.TopCollectionDTO;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin("*")
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public List<Collection> getAll() {
        return collectionService.getAllCollections();
    }

    @GetMapping("/{id}")
    public Collection getById(@PathVariable Long id) {
        return collectionService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    public Collection getBySlug(@PathVariable String slug) {
        return collectionService.getBySlug(slug);
    }

    @PostMapping
    public Collection create(@RequestBody Collection collection) {
        return collectionService.create(collection);
    }

    @PutMapping("/{id}")
    public Collection update(
            @PathVariable Long id,
            @RequestBody Collection collection) {

        return collectionService.update(id, collection);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        collectionService.delete(id);
    }

    @GetMapping("/featured")
    public Collection getFeaturedCollection() {
        return collectionService.getFeaturedCollection();
    }

    @PostMapping("/upload")
    public String uploadBanner(
        @RequestParam("file") MultipartFile file
    ) throws IOException {

    String fileName =
            UUID.randomUUID() + "_" + file.getOriginalFilename();

    Path uploadPath =
            Paths.get("uploads");

    Files.createDirectories(uploadPath);

    Path filePath =
            uploadPath.resolve(fileName);

    Files.copy(
            file.getInputStream(),
            filePath,
            StandardCopyOption.REPLACE_EXISTING
    );

    return "/uploads/" + fileName;
    }

    @GetMapping("/stats")
    public CollectionStatsDTO getStats() {
        return collectionService.getStats();
    }

    @GetMapping("/top-performance")
    public List<TopCollectionDTO> getTopCollections() {
        return collectionService.getTopCollections();
    }
}