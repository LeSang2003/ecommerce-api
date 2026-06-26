package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewsletterStatsResponse {

    private Long totalSubscribers;

    private Long activeSubscribers;

    private Long todaySubscribers;

    private Long monthSubscribers;
}