package com.demo.service.impl;

import com.demo.dto.lookbook.LookbookImageResponse;
import com.demo.dto.lookbook.LookbookResponse;
import com.demo.model.Lookbook;
import com.demo.model.LookbookImage;
import com.demo.model.LookbookSection;
import com.demo.repository.LookbookRepository;
import com.demo.service.LookbookService;
import org.springframework.stereotype.Service;
import com.demo.dto.lookbook.CreateLookbookRequest;
import com.demo.dto.lookbook.CreateLookbookImageRequest;
import com.demo.model.LayoutType;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.demo.dto.lookbook.LookbookStatsResponse;
import com.demo.repository.LookbookImageRepository;
import com.demo.dto.lookbook.LookbookSectionResponse;
import com.demo.dto.lookbook.CreateLookbookSectionRequest;

import com.demo.model.SectionType;
@Service
public class LookbookServiceImpl implements LookbookService {

        private final LookbookRepository lookbookRepository;
        private final LookbookImageRepository imageRepository;

        public LookbookServiceImpl(
        LookbookRepository lookbookRepository,
        LookbookImageRepository imageRepository
        ) {
                 this.lookbookRepository = lookbookRepository;
        this.imageRepository = imageRepository;
        }

    @Override
    public List<LookbookResponse> getAll() {

        return lookbookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
@Transactional(readOnly = true)
public LookbookResponse getBySlug(String slug) {

    Lookbook lookbook =
            lookbookRepository.findBySlug(slug)
                    .orElseThrow();

    System.out.println("SECTIONS = " + lookbook.getSections().size());

    for (LookbookSection s : lookbook.getSections()) {
        System.out.println(
                "SECTION ID = " + s.getId()
                + " IMAGES = "
                + s.getImages().size()
        );
    }

    return mapToResponse(lookbook);
}

    @Override
    public LookbookResponse getFeatured() {

        Lookbook lookbook =
                lookbookRepository.findByFeaturedTrue()
                        .orElseThrow();

        return mapToResponse(lookbook);
    }

    @Override
public LookbookResponse create(
        CreateLookbookRequest request
) {

    Lookbook lookbook = new Lookbook();

    lookbook.setTitle(request.getTitle());
    lookbook.setSlug(request.getSlug());
    lookbook.setSeason(request.getSeason());
    lookbook.setYear(request.getYear());
    lookbook.setDescription(request.getDescription());
    lookbook.setCoverImage(request.getCoverImage());

    if(Boolean.TRUE.equals(request.getFeatured())){
        lookbookRepository.clearFeatured();
        }
    lookbook.setFeatured(request.getFeatured());

    if (request.getImages() != null) {

        for (CreateLookbookImageRequest imgReq : request.getImages()) {

            LookbookImage image = new LookbookImage();

            image.setImageUrl(imgReq.getImageUrl());

            image.setDisplayOrder(
                    imgReq.getDisplayOrder()
            );

            image.setLayoutType(
                    LayoutType.valueOf(
                            imgReq.getLayoutType()
                    )
            );

            image.setLookbook(lookbook);

            lookbook.getImages().add(image);
        }
    }
    if (request.getSections() != null) {

    for (CreateLookbookSectionRequest secReq : request.getSections()) {

        LookbookSection section = new LookbookSection();

        section.setDisplayOrder(
                secReq.getDisplayOrder()
        );

        section.setType(
                SectionType.valueOf(
                        secReq.getType()
                )
        );

        section.setTitle(
                secReq.getTitle()
        );

        section.setContent(
                secReq.getContent()
        );

        section.setVideoUrl(
                secReq.getVideoUrl()
        );

        section.setLookbook(
                lookbook
        );

        // images trong section

        if (secReq.getImages() != null) {

            for (CreateLookbookImageRequest imgReq : secReq.getImages()) {

                LookbookImage image = new LookbookImage();

                image.setImageUrl(
                        imgReq.getImageUrl()
                );

                image.setDisplayOrder(
                        imgReq.getDisplayOrder()
                );

                image.setLayoutType(
                        LayoutType.valueOf(
                                imgReq.getLayoutType()
                        )
                );

                image.setSection(section);

                image.setLookbook(lookbook);

                section.getImages().add(image);
            }
        }

        lookbook.getSections().add(section);
    }
}

    Lookbook saved =
            lookbookRepository.save(lookbook);

    return mapToResponse(saved);
}
@Override
public LookbookResponse update(
        Long id,
        CreateLookbookRequest request
) {

    Lookbook lookbook =
            lookbookRepository
                    .findById(id)
                    .orElseThrow();

    lookbook.setTitle(request.getTitle());
    lookbook.setSlug(request.getSlug());
    lookbook.setSeason(request.getSeason());
    lookbook.setYear(request.getYear());
    lookbook.setDescription(request.getDescription());
    lookbook.setCoverImage(request.getCoverImage());
    if(Boolean.TRUE.equals(request.getFeatured())){
        lookbookRepository.clearFeatured();
        }

        lookbook.setFeatured(request.getFeatured());

    lookbook.getImages().clear();
    lookbook.getSections().clear();
    if (request.getImages() != null) {

        for (CreateLookbookImageRequest imgReq : request.getImages()) {

            LookbookImage image =
                    new LookbookImage();

            image.setImageUrl(
                    imgReq.getImageUrl()
            );

            image.setDisplayOrder(
                    imgReq.getDisplayOrder()
            );

            image.setLayoutType(
                    LayoutType.valueOf(
                            imgReq.getLayoutType()
                    )
            );

            image.setLookbook(lookbook);

            lookbook.getImages().add(image);
        }
    }
    if (request.getSections() != null) {

    for (CreateLookbookSectionRequest secReq : request.getSections()) {

        LookbookSection section = new LookbookSection();

        section.setDisplayOrder(
                secReq.getDisplayOrder()
        );

        section.setType(
                SectionType.valueOf(
                        secReq.getType()
                )
        );

        section.setTitle(
                secReq.getTitle()
        );

        section.setContent(
                secReq.getContent()
        );

        section.setVideoUrl(
                secReq.getVideoUrl()
        );

        section.setLookbook(
                lookbook
        );

        if (secReq.getImages() != null) {

            for (CreateLookbookImageRequest imgReq : secReq.getImages()) {

                LookbookImage image =
                        new LookbookImage();

                image.setImageUrl(
                        imgReq.getImageUrl()
                );

                image.setDisplayOrder(
                        imgReq.getDisplayOrder()
                );

                image.setLayoutType(
                        LayoutType.valueOf(
                                imgReq.getLayoutType()
                        )
                );

                image.setSection(section);

                image.setLookbook(lookbook);

                section.getImages().add(image);
            }
        }

        lookbook.getSections().add(section);
    }
}

    Lookbook saved =
            lookbookRepository.save(lookbook);

    return mapToResponse(saved);
}
@Override
public void delete(Long id) {

    lookbookRepository.deleteById(id);
}

    private LookbookResponse mapToResponse(
        Lookbook lookbook
) {

    LookbookResponse dto =
            new LookbookResponse();

    dto.setId(lookbook.getId());
    dto.setTitle(lookbook.getTitle());
    dto.setSlug(lookbook.getSlug());
    dto.setSeason(lookbook.getSeason());
    dto.setYear(lookbook.getYear());
    dto.setDescription(lookbook.getDescription());
    dto.setCoverImage(lookbook.getCoverImage());
    dto.setFeatured(lookbook.getFeatured());

    dto.setImages(
            lookbook.getImages()
                    .stream()
                    .map(this::mapImage)
                    .toList()
    );

    dto.setSections(
            lookbook.getSections()
                    .stream()
                    .map(this::mapSection)
                    .toList()
    );

    return dto;
}

    private LookbookImageResponse mapImage(
            LookbookImage image
    ) {

        LookbookImageResponse dto =
                new LookbookImageResponse();

        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setLayoutType(
                image.getLayoutType().name()
        );

        return dto;
    }

    @Override
public LookbookStatsResponse getStats() {

    long totalLookbooks =
            lookbookRepository.count();

    long totalImages =
            imageRepository.count();

    String featuredLookbook =
            lookbookRepository
                    .findByFeaturedTrue()
                    .map(Lookbook::getTitle)
                    .orElse("N/A");

    String latestSeason =
            lookbookRepository
                    .findFirstByOrderByYearDesc()
                    .map(l ->
                            l.getSeason()
                                    + " "
                                    + l.getYear()
                    )
                    .orElse("N/A");

    return new LookbookStatsResponse(
            totalLookbooks,
            totalImages,
            featuredLookbook,
            latestSeason
    );
}
private LookbookSectionResponse mapSection(
        LookbookSection section
) {
        System.out.println(
        "SECTION "
        + section.getId()
        + " TYPE="
        + section.getType()
        + " IMAGES="
        + section.getImages().size()
    );

    LookbookSectionResponse dto =
            new LookbookSectionResponse();

    dto.setId(section.getId());

    dto.setDisplayOrder(
            section.getDisplayOrder()
    );

    dto.setType(
            section.getType().name()
    );

    dto.setTitle(
            section.getTitle()
    );

    dto.setContent(
            section.getContent()
    );

    dto.setVideoUrl(
            section.getVideoUrl()
    );

    dto.setImages(
            section.getImages()
                    .stream()
                    .map(this::mapImage)
                    .toList()
    );

    return dto;
}
}