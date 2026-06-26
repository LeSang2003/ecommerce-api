package com.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;
}