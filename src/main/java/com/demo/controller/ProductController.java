package com.demo.controller;

import com.demo.model.Product;
import com.demo.service.ProductService;

import jakarta.validation.Valid;

import com.demo.dto.ProductRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.demo.model.Category;
import org.springframework.data.domain.Page;

import com.demo.model.ProductImage;
import com.demo.repository.ProductImageRepository;
import com.demo.repository.ProductRepository;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

   public ProductController(
        ProductService productService,
        ProductRepository productRepository,
        ProductImageRepository productImageRepository
    ) {
    this.productService = productService;
    this.productRepository = productRepository;
    this.productImageRepository = productImageRepository;
    }

    // =======================
    // PUBLIC API
    // =======================

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/collection/{slug}")
    public List<Product> getProductsByCollection(
        @PathVariable String slug
    ) {
        return productRepository.findByCollectionSlug(slug);
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }
    @GetMapping("/products/{id}/related")
    public List<Product> getRelatedProducts(@PathVariable Long id) {
        return productService.getRelatedProducts(id);
    }
    // =======================
    // ADMIN API
    // =======================

    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/admin/products/{id}")
    public Product updateProduct(
            @PathVariable Long id,
          @Valid @RequestBody ProductRequest request
    ) {
        return productService.update(id, request);
    }

    @DeleteMapping("/admin/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    //low stock products
    @GetMapping("/admin/products/low-stock")
    public List<Product> getLowStockProducts(){
        return productService.getLowStockProducts();
    }

    // Pagination + Search + Sort
    @GetMapping("/admin/products/search")
    public Page<Product> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long collectionId,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
    ) {

    return productService.search(
            keyword,
            categoryId,
            collectionId,
            minPrice,
            maxPrice,
            page,
            size,
            sort
        );
    }

    //upload a lot of img
    @PostMapping("/admin/products/{id}/images")
    public Product uploadProductImages(
        @PathVariable Long id,
        @RequestParam("files") List<MultipartFile> files
    ) throws Exception {

    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    List<ProductImage> images = new ArrayList<>();

    for (MultipartFile file : files) {

        String fileName =
                UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(
                file.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
        );

        ProductImage image = new ProductImage();

        image.setImageUrl("/uploads/" + fileName);

        image.setProduct(product);

        images.add(image);
    }

    productImageRepository.saveAll(images);

    return productRepository.findById(id).get();
    }

    

    @GetMapping("/categories-by-gender")
public List<Category> getCategoriesByGender(
        @RequestParam String gender
) {
    return productService.getCategoriesByGender(gender);
}
}

