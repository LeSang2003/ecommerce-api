package com.demo.service;

import com.demo.model.Product;
import com.demo.repository.ProductRepository;
import com.demo.repository.CategoryRepository;
import com.demo.repository.CollectionRepository;
import com.demo.repository.ColorRepository;
import com.demo.repository.SizeRepository;
import org.springframework.stereotype.Service;
import com.demo.dto.ProductRequest;
import java.util.HashSet;
import com.demo.model.Category;
import com.demo.repository.OrderItemRepository;
import com.demo.model.Review;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.demo.spec.ProductSpecification;
//search

import com.demo.model.Collection;
import org.springframework.data.domain.Sort;
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final OrderItemRepository orderItemRepository;
    private final CollectionRepository collectionRepository;
    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ColorRepository colorRepository,
            SizeRepository sizeRepository,
            OrderItemRepository orderItemRepository,
            CollectionRepository collectionRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.orderItemRepository = orderItemRepository;
        this.collectionRepository = collectionRepository;
    }

    // ===== COMMON =====
    public Product getById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
                )
            );
    }
    


    // ===== PUBLIC =====
    public List<Product> getAllProducts() {

     List<Product> products = productRepository.findAll(
        Sort.by(Sort.Direction.DESC, "id")
    );

    for (Product p : products) {

        Long sold =
                orderItemRepository.getSoldByProduct(p.getId());

        p.setSold(sold);

        double rating =
                p.getReviews()
                 .stream()
                 .mapToInt(Review::getRating)
                 .average()
                 .orElse(0);

        p.setRating(rating);
    }

    return products;
    }

    // ===== ADMIN =====
    public Product create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = getById(id);
        applyRequest(product, request);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    // ===== MAPPER =====
    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        product.setMaterial(request.getMaterial());
        product.setGender(request.getGender());

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category not found"
                )
            );

        product.setCategory(category);
        
        if (request.getCollectionId() != null) {

            Collection collection =
             collectionRepository.findById(request.getCollectionId())
                .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Collection not found"
                )
                 );

        product.setCollection(collection);

        } else {

            product.setCollection(null);
        }
        product.setColors(
            request.getColorIds() == null
            ? new HashSet<>()
            : new HashSet<>(
            colorRepository.findAllById(request.getColorIds())
            )
        );

        product.setSizes(
            request.getSizeIds() == null
            ? new HashSet<>()
            : new HashSet<>(
            sizeRepository.findAllById(request.getSizeIds())
            )
        );
    }

    //Low Stock Products
    public List<Product> getLowStockProducts(){
        return productRepository.findByStockLessThanOrderByStockAsc(10);
    }

    public Page<Product> search(
        String keyword,
        Long categoryId,
        Long collectionId,
        Double minPrice,
        Double maxPrice,
        int page,
        int size,
        String sort
) {

    Pageable pageable;

    // SORT
    if (sort != null && !sort.isBlank()) {

        String[] parts = sort.split(",");

        String field = parts[0];

        Sort.Direction direction =
                parts.length > 1 &&
                parts[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, field)
        );

    } else {

        pageable = PageRequest.of(page, size);

    }

    return productRepository.findAll(
            ProductSpecification.search(
                    keyword,
                    categoryId,
                    collectionId,
                    minPrice,
                    maxPrice
            ),
            pageable
    );
    }
    public List<Product> getRelatedProducts(Long productId) {
    Product product = getById(productId);

    return productRepository.findByCategoryIdAndIdNot(
        product.getCategory().getId(),
        productId,
        PageRequest.of(0, 4)
    );
}
public List<Category> getCategoriesByGender(String gender) {
    return productRepository.findCategoriesByGender(gender);
}
}
