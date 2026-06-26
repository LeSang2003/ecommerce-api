package com.demo.controller;

import com.demo.dto.MessageResponse;
import com.demo.dto.NewsletterRequest;
import com.demo.service.NewsletterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.demo.model.NewsletterSubscriber;
import com.demo.dto.NewsletterStatsResponse;
@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
@CrossOrigin
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    public ResponseEntity<MessageResponse> subscribe(
            @Valid @RequestBody NewsletterRequest request) {

        return ResponseEntity.ok(
                newsletterService.subscribe(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<NewsletterSubscriber>> getAll() {
      return ResponseEntity.ok(
            newsletterService.getAllSubscribers()
      );
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {

        return newsletterService.exportExcel();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
        @PathVariable Long id
    ) {

        newsletterService.deleteSubscriber(id);

        return ResponseEntity.ok(
            new MessageResponse(
                    "Subscriber deleted successfully"
            )
        );
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {

        return newsletterService.exportPdf();
    }

    @GetMapping("/stats")
    public ResponseEntity<NewsletterStatsResponse> stats() {

        return ResponseEntity.ok(
            newsletterService.getStats()
        );
    }
}