package com.demo.spec;

import com.demo.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> search(
            String keyword,
            Long categoryId,
            Long collectionId,
            Double minPrice,
            Double maxPrice
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // keyword
            if (keyword != null && !keyword.isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + keyword.toLowerCase() + "%"
                        )
                );
            }

            // category
            if (categoryId != null) {

                predicates.add(
                        cb.equal(
                                root.get("category").get("id"),
                                categoryId
                        )
                );
            }

            if (collectionId != null) {

                predicates.add(
                        cb.equal(
                                root.get("collection").get("id"),
                                collectionId
                        )
                );
            }
            // min price
            if (minPrice != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        )
                );
            }

            // max price
            if (maxPrice != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("price"),
                                maxPrice
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}