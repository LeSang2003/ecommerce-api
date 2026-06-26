package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContactStatsResponse {

    private Long totalMessages;

    private Long unreadMessages;

    private Long todayMessages;

    private Long monthMessages;
}