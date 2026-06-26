package com.demo.controller;

import com.demo.dto.ContactRequest;
import com.demo.dto.MessageResponse;
import com.demo.model.ContactMessage;
import com.demo.service.ContactService;
import com.demo.dto.ReplyContactRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.demo.dto.ContactStatsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestBody ContactRequest request
    ) {

        return ResponseEntity.ok(
                contactService.send(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAll() {

        return ResponseEntity.ok(
                contactService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
        @PathVariable Long id
    ) {

        contactService.delete(id);

        return ResponseEntity.ok(
            new MessageResponse(
                    "Message deleted successfully"
            )
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<MessageResponse> markAsRead(
        @PathVariable Long id
    ) {

        contactService.markAsRead(id);

        return ResponseEntity.ok(
            new MessageResponse(
                    "Marked as read"
            )
        );
    }

    @PostMapping("/reply")
    public ResponseEntity<MessageResponse> reply(
        @RequestBody ReplyContactRequest request
    ) {

        return ResponseEntity.ok(
            contactService.reply(request)
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ContactStatsResponse> stats() {

        return ResponseEntity.ok(
            contactService.getStats()
        );
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {

        return contactService.exportExcel();
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {

        return contactService.exportPdf();
    }
}