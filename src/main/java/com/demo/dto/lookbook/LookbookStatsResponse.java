package com.demo.dto.lookbook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LookbookStatsResponse {

    private Long totalLookbooks;

    private Long totalImages;

    private String featuredLookbook;

    private String latestSeason;
}