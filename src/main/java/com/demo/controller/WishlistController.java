package com.demo.controller;

import com.demo.model.User;
import com.demo.model.Wishlist;
import com.demo.repository.UserRepository;
import com.demo.service.WishlistService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.demo.dto.WishlistStatsResponse;
import com.demo.dto.TopWishlistProductDTO;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    private final UserRepository userRepository;

    // TOGGLE
    @PostMapping("/{productId}")
    public String toggleWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return wishlistService.toggleWishlist(
                user,
                productId
        );
    }

    // GET MY WISHLIST
    @GetMapping
    public List<Wishlist> getMyWishlist(
            Authentication authentication
    ) {

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return wishlistService.getMyWishlist(
                user
        );
    }

    // CHECK
    @GetMapping("/check/{productId}")
    public boolean checkWishlist(
            @PathVariable Long productId,
            Authentication authentication
    ) {

        String username =
                authentication.getName();

        User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return wishlistService.isWishlisted(
                user,
                productId
        );
    }
  @DeleteMapping("/{productId}")
public String removeWishlist(
        @PathVariable Long productId,
        Authentication authentication
) {

    String username =
            authentication.getName();

    User user =
            userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found"
                            )
                    );

    return wishlistService.removeWishlist(
            user,
            productId
    );
}

@GetMapping("/stats")
public WishlistStatsResponse getWishlistStats() {
    return wishlistService.getWishlistStats();
}

@GetMapping("/top-products")
public List<TopWishlistProductDTO> getTopWishlistProducts() {
    return wishlistService.getTopWishlistProducts();
}
}