package com.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsletterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
}