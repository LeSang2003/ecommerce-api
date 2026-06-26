package com.demo.service;

import com.demo.dto.MessageResponse;
import com.demo.dto.NewsletterRequest;
import com.demo.model.NewsletterSubscriber;
import com.demo.repository.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import com.demo.dto.NewsletterStatsResponse;
import java.time.LocalDate;
@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriberRepository repository;
    private final MailService mailService;
    public MessageResponse subscribe(NewsletterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        Optional<NewsletterSubscriber> existing =
                repository.findByEmail(email);

        if (existing.isPresent()) {

            NewsletterSubscriber subscriber =
                    existing.get();

            if (Boolean.TRUE.equals(
                    subscriber.getActive())) {

                return new MessageResponse(
                        "You are already subscribed."
                );
            }

            subscriber.setActive(true);
subscriber.setSubscribedAt(
        LocalDateTime.now()
);

repository.save(subscriber);

try {
    mailService.sendNewsletterWelcomeEmail(
            subscriber.getEmail()
    );
} catch (Exception e) {
    e.printStackTrace();
}

return new MessageResponse(
        "Subscription reactivated."
);
        }

        NewsletterSubscriber subscriber =
                new NewsletterSubscriber();

        subscriber.setEmail(email);
        subscriber.setSubscribedAt(
                LocalDateTime.now()
        );
        subscriber.setActive(true);

       repository.save(subscriber);

        try {
                mailService.sendNewsletterWelcomeEmail(
                subscriber.getEmail()
                        );
        } catch (Exception e) {
                e.printStackTrace();
        }

        return new MessageResponse(
                        "Thank you for subscribing ❤️"
                );
    }
  public List<NewsletterSubscriber> getAllSubscribers() {
    return repository.findAll(
            Sort.by(Sort.Direction.DESC, "subscribedAt")
    );
  }
public ResponseEntity<byte[]> exportExcel() {

    try {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Subscribers");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Subscribed At");

        List<NewsletterSubscriber> subscribers =
                repository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "subscribedAt"
                        )
                );

        int rowNum = 1;

        for (NewsletterSubscriber s : subscribers) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(s.getId());

            row.createCell(1)
                    .setCellValue(s.getEmail());

            row.createCell(2)
                    .setCellValue(
                            s.getSubscribedAt()
                                    .toString()
                    );
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=newsletter.xlsx"
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(out.toByteArray());

    } catch (Exception e) {

        throw new RuntimeException(
                "Failed to export excel",
                e
        );
    }
        }

        public void deleteSubscriber(Long id) {

    NewsletterSubscriber subscriber =
            repository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Subscriber not found"));

    subscriber.setActive(false);

    repository.save(subscriber);
        

}

public ResponseEntity<byte[]> exportPdf() {

    try {

        List<NewsletterSubscriber> subscribers =
                repository.findAll(
                        Sort.by(
                                Sort.Direction.DESC,
                                "subscribedAt"
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
                        "HYNO Newsletter Subscribers"
                )
        );

        document.add(
                new Paragraph(" ")
        );

        PdfPTable table =
                new PdfPTable(3);

        table.addCell("ID");
        table.addCell("Email");
        table.addCell("Subscribed At");

        for (
                NewsletterSubscriber s
                        : subscribers
        ) {

            table.addCell(
                    String.valueOf(
                            s.getId()
                    )
            );

            table.addCell(
                    s.getEmail()
            );

            table.addCell(
                    String.valueOf(
                            s.getSubscribedAt()
                    )
            );
        }

        document.add(table);

        document.add(
                new Paragraph(
                        "\nTotal Subscribers: "
                                + subscribers.size()
                )
        );

        document.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=newsletter.pdf"
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        out.toByteArray()
                );

    } catch (Exception e) {

        throw new RuntimeException(
                "Failed to export pdf",
                e
        );
    }
}
  public NewsletterStatsResponse getStats() {

    long total =
            repository.count();

    long active =
            repository.countByActiveTrue();

    long today =
            repository.countBySubscribedAtAfter(
                    LocalDate.now()
                            .atStartOfDay()
            );

    long month =
            repository.countBySubscribedAtAfter(
                    LocalDate.now()
                            .withDayOfMonth(1)
                            .atStartOfDay()
            );

    return new NewsletterStatsResponse(
            total,
            active,
            today,
            month
    );
}
}