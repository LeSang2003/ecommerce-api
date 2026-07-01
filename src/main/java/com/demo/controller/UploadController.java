package com.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UploadController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file)
        throws IOException {

    System.out.println("=== UPLOAD API CALLED ===");

    if (file == null || file.isEmpty()) {
        throw new RuntimeException("File is empty");
    }

    // LOG tên file
    System.out.println("File name: " + file.getOriginalFilename());

    String uploadDir =
            System.getProperty("user.dir") + "/uploads/";

    // LOG đường dẫn lưu
    System.out.println("Upload dir: " + uploadDir);

    File dir = new File(uploadDir);

    if (!dir.exists()) {
        dir.mkdirs();
    }

    String filename =
            UUID.randomUUID() + "_" +
            file.getOriginalFilename();

    System.out.println("New filename: " + filename);

    File dest = new File(uploadDir + filename);

    // LOG file đích
    System.out.println("Destination: " + dest.getAbsolutePath());

    file.transferTo(dest);

    System.out.println("UPLOAD SUCCESS");

    return "http://${import.meta.env.VITE_API_HOST}/uploads/" + filename;
  }
}