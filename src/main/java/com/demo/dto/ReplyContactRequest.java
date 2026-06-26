package com.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyContactRequest {

    private Long contactId;

    private String email;

    private String subject;

    private String message;
}