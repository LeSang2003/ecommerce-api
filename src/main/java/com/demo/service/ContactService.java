package com.demo.service;

import com.demo.dto.ContactRequest;
import com.demo.dto.MessageResponse;
import com.demo.model.ContactMessage;
import com.demo.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.demo.dto.ReplyContactRequest;
import java.time.LocalDateTime;
import java.util.List;
import com.demo.dto.ContactStatsResponse;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository repository;
    private final MailService mailService;

    public MessageResponse send(ContactRequest request) {

        ContactMessage message =
                new ContactMessage();

        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setSubject(request.getSubject());
        message.setMessage(request.getMessage());

        message.setCreatedAt(
                LocalDateTime.now()
        );

        repository.save(message);

        try {

            mailService.sendContactNotification(
                    request.getName(),
                    request.getEmail(),
                    request.getSubject(),
                    request.getMessage()
            );

             // gửi cho khách
            mailService.sendContactConfirmationEmail(
                    request.getEmail(),
                    request.getName()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return new MessageResponse(
                "Message sent successfully"
        );
    }

    public List<ContactMessage> getAll() {

        return repository.findAll(
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }

    public void markAsRead(Long id) {

    ContactMessage message =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Message not found"
                            ));

    message.setReadStatus(true);

    repository.save(message);
    }
   public MessageResponse reply(
        ReplyContactRequest request
) {

    mailService.sendReplyEmail(
            request.getEmail(),
            request.getSubject(),
            request.getMessage()
    );

    ContactMessage contact =
            repository.findById(
                    request.getContactId()
            )
            .orElseThrow(() ->
                    new RuntimeException(
                            "Contact not found"
                    ));

    contact.setReplied(true);

    contact.setReplyContent(
            request.getMessage()
    );

    contact.setRepliedAt(
            LocalDateTime.now()
    );

    contact.setRepliedBy(
            "Admin"
    );

    repository.save(contact);

    return new MessageResponse(
            "Reply sent successfully"
    );
}

public ContactStatsResponse getStats() {

    long total =
            repository.count();

    long unread =
            repository.countByReadStatusFalse();

    long today =
            repository.countByCreatedAtAfter(
                    LocalDate.now()
                            .atStartOfDay()
            );

    long month =
            repository.countByCreatedAtAfter(
                    LocalDate.now()
                            .withDayOfMonth(1)
                            .atStartOfDay()
            );

    return new ContactStatsResponse(
            total,
            unread,
            today,
            month
    );
}

public ResponseEntity<byte[]> exportExcel() {

    try {

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet(
                        "Contact Messages"
                );

        Row header =
                sheet.createRow(0);

        header.createCell(0)
                .setCellValue("ID");

        header.createCell(1)
                .setCellValue("Name");

        header.createCell(2)
                .setCellValue("Email");

        header.createCell(3)
                .setCellValue("Subject");

        header.createCell(4)
                .setCellValue("Read");

        header.createCell(5)
                .setCellValue("Replied");

        header.createCell(6)
                .setCellValue("Replied At");

        header.createCell(7)
                .setCellValue("Created At");

        List<ContactMessage> messages =
                repository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        int rowNum = 1;

        for (ContactMessage m : messages) {

            Row row =
                    sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(m.getId());

            row.createCell(1)
                    .setCellValue(m.getName());

            row.createCell(2)
                    .setCellValue(m.getEmail());

            row.createCell(3)
                    .setCellValue(m.getSubject());

            row.createCell(4)
        .setCellValue(
                Boolean.TRUE.equals(m.getReadStatus())
                        ? "READ"
                        : "NEW"
        );

            row.createCell(5)
    .setCellValue(
        Boolean.TRUE.equals(m.getReplied())
            ? "YES"
            : "NO"
    );

            row.createCell(6)
                    .setCellValue(
                            m.getRepliedAt() != null
                                    ? m.getRepliedAt().toString()
                                    : "-"
                    );

            row.createCell(7)
                    .setCellValue(
                            m.getCreatedAt().toString()
                    );
        }

        for (int i = 0; i <= 7; i++) {

            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=contact.xlsx"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(
                        out.toByteArray()
                );

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(
                "Export failed",
                e
        );
    }
}

public ResponseEntity<byte[]> exportPdf() {

    try {

        List<ContactMessage> messages =
                repository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        Document document =
                new Document();

        PdfWriter.getInstance(
                document,
                out
        );

        document.open();

        document.add(
                new Paragraph(
                        "HYNO Contact Messages"
                )
        );

        document.add(
                new Paragraph(" ")
        );

        PdfPTable table =
                new PdfPTable(7);

        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Email");
        table.addCell("Subject");
        table.addCell("Read");
        table.addCell("Replied");
        table.addCell("Replied At");

        for (ContactMessage m : messages) {

            table.addCell(
                    String.valueOf(
                            m.getId()
                    )
            );

            table.addCell(
                    m.getName()
            );

            table.addCell(
                    m.getEmail()
            );

            table.addCell(
                    m.getSubject()
            );

            table.addCell(
        Boolean.TRUE.equals(m.getReadStatus())
                ? "READ"
                : "NEW"
);

           table.addCell(
    Boolean.TRUE.equals(m.getReplied())
        ? "YES"
        : "NO"
);

            table.addCell(
                    m.getRepliedAt() != null
                            ? m.getRepliedAt().toString()
                            : "-"
            );
        }

        document.add(table);

        document.add(
                new Paragraph(
                        "\nTotal Messages: "
                                + messages.size()
                )
        );

        document.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=contact.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        out.toByteArray()
                );

    } catch (Exception e) {
         e.printStackTrace();
        throw new RuntimeException(
                "Export pdf failed",
                e
        );
    }
}
}