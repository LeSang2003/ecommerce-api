package com.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.demo.dto.CollectionStatsDTO;
import com.demo.model.Collection;
import com.demo.repository.CollectionRepository;
import com.demo.repository.OrderItemRepository;
import com.demo.dto.CollectionPerformanceDTO;
import com.demo.dto.TopCollectionDTO;
@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final OrderItemRepository orderItemRepository;

    public CollectionService(
        CollectionRepository collectionRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.collectionRepository = collectionRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    public Collection getById(Long id) {
        return collectionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Collection not found"));
    }

    public Collection getBySlug(String slug) {
        return collectionRepository.findBySlug(slug);
    }

    public Collection create(Collection collection) {

        if (collectionRepository.findBySlug(collection.getSlug()) != null) {
            throw new RuntimeException("Collection slug already exists");
        }

         return collectionRepository.save(collection);
    }

    public Collection update(Long id, Collection data) {

        Collection c = getById(id);
         // CHECK SLUG TRÙNG
        Collection existed = collectionRepository.findBySlug(data.getSlug());

        if (existed != null && !existed.getId().equals(id)) {
            throw new RuntimeException("Collection slug already exists");
        }

        c.setName(data.getName());
        c.setSlug(data.getSlug());

        c.setBannerImage(data.getBannerImage());
        c.setDescription(data.getDescription());

        c.setSeason(data.getSeason());
        c.setYear(data.getYear());

        c.setFeatured(data.getFeatured());

        return collectionRepository.save(c);
    }

    public void delete(Long id) {
        collectionRepository.deleteById(id);
    }

    public Collection getFeaturedCollection() {
        return collectionRepository.findFirstByFeaturedTrue();
    }

    public CollectionStatsDTO getStats() {

    Long totalCollections = collectionRepository.count();
    Long featuredCollections = collectionRepository.countByFeaturedTrue();
    Long totalProducts = collectionRepository.countProductsInCollections();

    List<String> largestCollections =
            collectionRepository.findLargestCollections();

    String largestCollection =
            largestCollections.isEmpty()
                    ? "N/A"
                    : largestCollections.get(0);

    List<CollectionPerformanceDTO> performance =
            orderItemRepository.getCollectionPerformance();

    String bestSellingCollection = "N/A";
    String highestRevenueCollection = "N/A";

    if (!performance.isEmpty()) {
        bestSellingCollection = performance.get(0).getCollectionName();

        highestRevenueCollection = performance.stream()
                .max((a, b) -> Double.compare(
                        a.getTotalRevenue(),
                        b.getTotalRevenue()))
                .map(CollectionPerformanceDTO::getCollectionName)
                .orElse("N/A");
    }

    return new CollectionStatsDTO(
            totalCollections,
            featuredCollections,
            totalProducts,
            largestCollection,
            bestSellingCollection,
            highestRevenueCollection
    );
}

public List<TopCollectionDTO> getTopCollections() {
    return orderItemRepository.getTopCollections();
}
}