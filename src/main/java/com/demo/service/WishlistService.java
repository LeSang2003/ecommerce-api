package com.demo.service;

import com.demo.model.Product;
import com.demo.model.User;
import com.demo.model.Wishlist;
import com.demo.repository.ProductRepository;
import com.demo.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.demo.dto.WishlistStatsResponse;
import com.demo.dto.TopWishlistProductDTO;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    private final ProductRepository productRepository;

    // TOGGLE
    public String toggleWishlist(
            User user,
            Long productId
    ) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );

        boolean exists =
                wishlistRepository.existsByUserAndProduct(
                        user,
                        product
                );

        if (exists) {

            wishlistRepository.deleteByUserAndProduct(
                    user,
                    product
            );

            return "Removed from wishlist";
        }

        Wishlist wishlist = new Wishlist();

        wishlist.setUser(user);

        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);

        return "Added to wishlist";
    }

    // GET USER WISHLIST
    public List<Wishlist> getMyWishlist(
            User user
    ) {

        return wishlistRepository.findByUser(user);
    }

    // CHECK
    public boolean isWishlisted(
            User user,
            Long productId
    ) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );

        return wishlistRepository.existsByUserAndProduct(
                user,
                product
        );
    }

    @Transactional
    public String removeWishlist(
            User user,
            Long productId
    ) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );

        wishlistRepository.deleteByUserAndProduct(
                user,
                product
        );

        return "Removed";
    }

    public WishlistStatsResponse getWishlistStats() {

    WishlistStatsResponse res =
            new WishlistStatsResponse();

    long totalItems =
            wishlistRepository.count();

    Long uniqueUsers =
            wishlistRepository.countUniqueUsers();

    List<String> most =
            wishlistRepository.findMostWishedProduct();

    res.setTotalWishlists(uniqueUsers);
    res.setTotalItems(totalItems);
    res.setUniqueUsers(uniqueUsers);

    res.setMostWishedProduct(
            most.isEmpty() ? "N/A" : most.get(0)
    );

    if (uniqueUsers == 0) {
        res.setAverageItemsPerUser(0.0);
    } else {
        res.setAverageItemsPerUser(
                (double) totalItems / uniqueUsers
        );
    }

    return res;
}

public List<TopWishlistProductDTO> getTopWishlistProducts() {
    return wishlistRepository.getTopWishlistProducts();
}
}