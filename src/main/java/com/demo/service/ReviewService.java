package com.demo.service;

import com.demo.dto.CreateReviewRequest;
import com.demo.dto.ReviewRequest;
import com.demo.dto.UpdateReviewRequest;
import com.demo.dto.ReviewResponse;
import com.demo.model.Order;
import com.demo.model.OrderStatus;
import com.demo.model.Product;
import com.demo.model.Review;
import com.demo.model.User;
import com.demo.repository.OrderRepository;
import com.demo.repository.ProductRepository;
import com.demo.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    // =========================
    // CREATE REVIEW
    // =========================

    public ReviewResponse createReview(
            User user,
            CreateReviewRequest request
    ) {

        // PRODUCT
        Product product =
                productRepository.findById(
                        request.getProductId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        )
                );

        // ORDER
        Order order =
                orderRepository.findById(
                        request.getOrderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        )
                );

        // CHECK OWNER
        if (
                !order.getUser().getId()
                        .equals(user.getId())
        ) {

            throw new RuntimeException(
                    "This order is not yours"
            );
        }

        // CHECK COMPLETED
        if (
                order.getStatus()
                        != OrderStatus.COMPLETED
        ) {

            throw new RuntimeException(
                    "Only completed orders can review"
            );
        }

        // CHECK PRODUCT IN ORDER
        boolean bought =
                order.getItems().stream().anyMatch(
                        item ->
                                item.getProduct()
                                        .getId()
                                        .equals(
                                                product.getId()
                                        )
                );

        if (!bought) {

            throw new RuntimeException(
                    "You did not buy this product"
            );
        }

        // CHECK ALREADY REVIEWED
        if (
                reviewRepository.findByUserAndProduct(
                        user,
                        product
                ).isPresent()
        ) {

            throw new RuntimeException(
                    "You already reviewed this product"
            );
        }

        // CHECK RATING
        if (
                request.getRating() < 1
                || request.getRating() > 5
        ) {

            throw new RuntimeException(
                    "Rating must be 1-5"
            );
        }

        // CREATE REVIEW
        Review review = new Review();

        review.setUser(user);

        review.setProduct(product);

        review.setOrder(order);

        review.setRating(
                request.getRating()
        );

        review.setComment(
                request.getComment()
        );
        review.setImageUrl(
                request.getImageUrl()
        );

        review.setCreatedAt(
                LocalDateTime.now()
        );

        reviewRepository.save(review);

        // RESPONSE
        ReviewResponse response =
                new ReviewResponse();

        response.setId(review.getId());

        response.setUserId(
                user.getId()
        );
        response.setUsername(
                user.getUsername()
        );

        response.setAvatar(
                user.getAvatar()
        );

        response.setRating(
                review.getRating()
        );

        response.setComment(
                review.getComment()
        );

        response.setImageUrl(
                review.getImageUrl()
        );
        response.setCreatedAt(
                review.getCreatedAt()
        );

        return response;
    }

    // =========================
    // GET REVIEWS BY PRODUCT
    // =========================

    public List<ReviewResponse> getReviewsByProduct(
            Long productId
    ) {

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                )
                        );

        return reviewRepository
                .findByProductOrderByCreatedAtDesc(
                        product
                )
                .stream()
                .map(review -> {

                    ReviewResponse res =
                            new ReviewResponse();

                    res.setId(review.getId());

                    res.setUserId(
                        review.getUser().getId()
                    );

                    res.setUsername(
                            review.getUser()
                                    .getUsername()
                    );
                    res.setAvatar(
                           review.getUser()
                                    .getAvatar()
                    );
                    res.setRating(
                            review.getRating()
                    );

                    res.setComment(
                            review.getComment()
                    );
                    res.setImageUrl(
                                review.getImageUrl()
                        );

                    res.setCreatedAt(
                            review.getCreatedAt()
                    );

                    return res;

                }).toList();
    }

    // =========================
    // AVG RATING
    // =========================

    public Double getAverageRating(
            Long productId
    ) {

        Double avg =
                reviewRepository.getAverageRating(
                        productId
                );

        return avg != null ? avg : 0.0;
    }

  public ReviewResponse updateReview(
        Long reviewId,
        User user,
        UpdateReviewRequest request
) {

    Review review =
            reviewRepository.findById(reviewId)
                    .orElseThrow(() ->
                            new RuntimeException("Review not found")
                    );

    if (
            !review.getUser().getId()
                    .equals(user.getId())
    ) {
        throw new RuntimeException(
                "You can only edit your own review"
        );
    }

    review.setRating(
            request.getRating()
    );

    review.setComment(
            request.getComment()
    );
    review.setImageUrl(
        request.getImageUrl()
    );
   

    reviewRepository.save(review);

    ReviewResponse res =
            new ReviewResponse();

    res.setId(review.getId());
    res.setUserId(
        review.getUser().getId()
);
    res.setUsername(
            review.getUser().getUsername()
    );

    res.setAvatar(
            review.getUser().getAvatar()
    );

    res.setRating(
            review.getRating()
    );

    res.setComment(
            review.getComment()
    );
    res.setImageUrl(
                review.getImageUrl()
        );
    res.setCreatedAt(
            review.getCreatedAt()
    );

    return res;
        }

        public void deleteReview(
        Long reviewId,
        User user
) {

    Review review =
            reviewRepository.findById(reviewId)
                    .orElseThrow(() ->
                            new RuntimeException("Review not found")
                    );

    if (
            !review.getUser().getId()
                    .equals(user.getId())
    ) {
        throw new RuntimeException(
                "You can only delete your own review"
        );
    }

    reviewRepository.delete(review);
}
}