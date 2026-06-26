package com.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    private String fullName;

    private String phone;

    private String address;

    private LocalDate birthday;

    private String gender;
}