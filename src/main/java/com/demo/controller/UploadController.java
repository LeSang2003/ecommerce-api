package com.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UploadController {

    private final Cloudinary cloudinary;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file)
        throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.emptyMap()
        );

        return (String) uploadResult.get("secure_url");
    }
}