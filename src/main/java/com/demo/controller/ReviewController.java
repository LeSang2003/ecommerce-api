package com.demo.controller;

import com.demo.dto.CreateReviewRequest;
import com.demo.dto.UpdateReviewRequest;
import com.demo.dto.ReviewResponse;
import com.demo.model.User;
import com.demo.service.ReviewService;
import com.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    private final UserService userService;

    // =========================
    // CREATE REVIEW
    // =========================

    @PostMapping
    public ReviewResponse createReview(
            @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {

        User user = userService.findByUsername(
                authentication.getName()
        );

        return reviewService.createReview(
                user,
                request
        );
    }

    // =========================
    // GET REVIEWS BY PRODUCT
    // =========================

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getReviewsByProduct(
            @PathVariable Long productId
    ) {

        return reviewService.getReviewsByProduct(
                productId
        );
    }

    // =========================
    // AVG RATING
    // =========================

    @GetMapping("/product/{productId}/average")
    public Double getAverageRating(
            @PathVariable Long productId
    ) {

        return reviewService.getAverageRating(
                productId
        );
    }


       @PutMapping("/{id}")
public ReviewResponse updateReview(
        @PathVariable Long id,
        @RequestBody UpdateReviewRequest request,
        Authentication authentication
) {

        User user =
            userService.findByUsername(
                    authentication.getName()
            );

                return reviewService.updateReview(
                id,
                user,
                request
        );
        }

        @DeleteMapping("/{id}")
public void deleteReview(
        @PathVariable Long id,
        Authentication authentication
) {

    User user =
            userService.findByUsername(
                    authentication.getName()
            );

    reviewService.deleteReview(
            id,
            user
    );
}

}