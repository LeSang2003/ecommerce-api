package com.demo.service;

import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.List;
import com.demo.model.OrderItem;
import com.demo.model.OrderStatus;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(
            String to,
            String token
    ) {

        String verifyLink =
                "http://localhost:5173/verify-email?token="
                        + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);

        message.setSubject(
                "Verify your account"
        );

        message.setText(
                "Click link below to verify:\n\n"
                        + verifyLink
        );

        mailSender.send(message);
    }
    public void sendResetPasswordEmail(
        String to,
        String token
        ) {

    String resetLink =
            "http://localhost:5173/reset-password?token="
                    + token;

    SimpleMailMessage message =
            new SimpleMailMessage();

    message.setTo(to);

    message.setSubject("Reset Password");

    message.setText(
            "Click link below to reset password:\n\n"
                    + resetLink
    );

    mailSender.send(message);
}
public void sendPaymentSuccessEmail(
        String to,
        Long orderId,
        Double total,
        List<OrderItem> items
) {
    String orderLink =
        "http://localhost:5173/orders/" + orderId;
        if (items == null) {
                items = java.util.Collections.emptyList();
        }
        StringBuilder productRows = new StringBuilder();

for (OrderItem item : items) {

    productRows.append("""
        <tr>
            <td style="padding:10px;border-bottom:1px solid #eee;">
                %s
            </td>

            <td style="padding:10px;text-align:center;border-bottom:1px solid #eee;">
                %d
            </td>

            <td style="padding:10px;text-align:center;border-bottom:1px solid #eee;">
                %s
            </td>

            <td style="padding:10px;text-align:right;border-bottom:1px solid #eee;">
                %s VND
            </td>
        </tr>
        """.formatted(
            item.getProduct().getName(),
            item.getQuantity(),
            item.getSize() == null ? "-" : item.getSize(),
            String.format("%,.0f",
                    item.getPrice() * item.getQuantity())
        ));
}

    String html = """
        <div style="max-width:600px;margin:auto;
                    font-family:Arial,sans-serif;
                    border:1px solid #eee;
                    padding:30px">

            <div style="
    background:#111827;
    padding:25px;
    text-align:center;
    border-radius:12px 12px 0 0;">

    <h1 style="
        margin:0;
        color:white;
        letter-spacing:4px;
        font-size:34px;">
        HYNO
    </h1>

    <p style="
        margin-top:8px;
        color:#d1d5db;
        font-size:14px;">
        Modern Fashion & Lifestyle
    </p>

</div>

<div style="padding:30px;">

    <h2 style="color:#28a745;">
        ✅ Payment Successful
    </h2>

    <p>
        Thank you for shopping with HYNO.
        Your payment has been confirmed.
    </p>

            <hr>

            <table style="width:100%%;line-height:30px;">
                <tr>
                    <td><b>Order ID</b></td>
                    <td>#%d</td>
                </tr>

                <tr>
                    <td><b>Status</b></td>
                    <td>CONFIRMED</td>
                </tr>

                <tr>
    <td><b>Total</b></td>
    <td style="
        color:#dc2626;
        font-weight:bold;
        font-size:18px;">
        %s VND
    </td>
</tr>
            </table>
            <h3 style="margin-top:25px;">
    Order Items
</h3>

<table style="
        width:100%%;
        border-collapse:collapse;
        margin-top:10px;
        font-size:14px;">

    <tr style="background:#f8f9fa;">
        <th style="padding:10px;text-align:left;">
            Product
        </th>

        <th style="padding:10px;text-align:center;">
            Quantity
        </th>

        <th style="padding:10px;text-align:center;">
            Size
        </th>

        <th style="padding:10px;text-align:right;">
            Total
        </th>
    </tr>

    %s

</table>



            <hr>

            <p style="font-size:14px;color:#666;">
                We'll notify you when your order status changes.
            </p>

            <div style="margin-top:30px;
                        text-align:center;
                        color:#999;
                        font-size:13px;">
                © 2026 HYNO Fashion Store
            </div>
        </div>
        </div>
        """.formatted(
             orderId,
    String.format("%,.0f", total),
    productRows.toString(),
    orderLink
    );

    sendHtmlEmail(
            to,
            "HYNO - Payment Successful",
            html
    );
}

public void sendNewsletterWelcomeEmail(String to) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(to);

    message.setSubject("Welcome to HYNO Newsletter ❤️");

    message.setText(
            """
            Thank you for subscribing to HYNO.

            You'll receive:
            • New collections
            • Exclusive offers
            • Product launches

            Stay tuned!

            HYNO Team
            """
    );

    mailSender.send(message);
}

public void sendContactNotification(
        String name,
        String email,
        String subject,
        String content
) {

    SimpleMailMessage message =
            new SimpleMailMessage();

    message.setTo("le657930@gmail.com");

    message.setSubject(
            "New Contact Message - HYNO"
    );

    message.setText(
            "Name: " + name + "\n" +
            "Email: " + email + "\n" +
            "Subject: " + subject + "\n\n" +
            content
    );

    mailSender.send(message);
}

public void sendContactConfirmationEmail(
        String to,
        String name
) {

    SimpleMailMessage message =
            new SimpleMailMessage();

    message.setTo(to);

    message.setSubject(
            "HYNO Contact Confirmation"
    );

    message.setText(
            """
            Hi %s,

            Thank you for contacting HYNO.

            We have received your message and our team will get back to you shortly.

            HYNO Team
            """
            .formatted(name)
    );

    mailSender.send(message);
}
public void sendReplyEmail(
        String to,
        String subject,
        String content
) {

    SimpleMailMessage message =
            new SimpleMailMessage();

    message.setTo(to);

    message.setSubject(subject);

    message.setText(content);

    mailSender.send(message);
}

private void sendHtmlEmail(
        String to,
        String subject,
        String html
) {
    try {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);

    } catch (MessagingException e) {
        throw new RuntimeException("Cannot send email", e);
    }
}

public void sendOrderStatusEmail(
        String to,
        Long orderId,
        OrderStatus status
) {

    String color = "#0d6efd";

    String message = "Your order status has been updated.";
        String icon = "📦";
        String orderLink =
    "http://localhost:5173/orders/" + orderId;
    String timeline = "";
if (status == OrderStatus.CONFIRMED) {
    color = "#0d6efd";
    icon = "✅";
    message = "Your order has been confirmed.";
    timeline =
"""
🟢 Confirmed
&nbsp;&nbsp;→&nbsp;&nbsp;
⚪ Shipping
&nbsp;&nbsp;→&nbsp;&nbsp;
⚪ Completed
""";
}

if (status == OrderStatus.SHIPPING) {
    color = "#fd7e14";
    icon = "🚚";
    message = "Your order is on the way.";
    timeline =
"""
🟢 Confirmed
&nbsp;&nbsp;→&nbsp;&nbsp;
🟢 Shipping
&nbsp;&nbsp;→&nbsp;&nbsp;
⚪ Completed
""";
}

if (status == OrderStatus.COMPLETED) {
    color = "#28a745";
    icon = "🎉";
    message = "Your order has been delivered successfully.";
    timeline =
"""
🟢 Confirmed
&nbsp;&nbsp;→&nbsp;&nbsp;
🟢 Shipping
&nbsp;&nbsp;→&nbsp;&nbsp;
🟢 Completed
""";
}

if (status == OrderStatus.CANCELLED) {
    color = "#dc3545";
    icon = "❌";
    message = "Your order has been cancelled.";
    timeline =
"""
❌ Order Cancelled
""";
}

   

    String html = """
       <div style="
    max-width:640px;
    margin:30px auto;
    background:#ffffff;
    border:1px solid #e5e7eb;
    border-radius:12px;
    overflow:hidden;
    font-family:Arial,sans-serif;
    box-shadow:0 2px 10px rgba(0,0,0,0.05);">

           <div style="
    background:#111827;
    padding:25px;
    text-align:center;
    border-radius:12px 12px 0 0;">

    <h1 style="
        margin:0;
        color:white;
        letter-spacing:4px;
        font-size:34px;">
        HYNO
    </h1>

    <p style="
        margin-top:8px;
        color:#d1d5db;
        font-size:14px;">
        Modern Fashion & Lifestyle
    </p>

</div>
  <!-- Content -->
    <div style="padding:30px;">
           <h2 style="color:%s;">
                 %s Order Update
        </h2>

            <p>
                 %s
                </p>

            <p>
                <b>Order ID:</b> #%d
            </p>

            <p>
                <b>Current Status:</b>
                <span style="
                    background:%s;
                    color:#ffffff;
                    padding:8px 18px;
                    border-radius:20px;
                    font-size:13px;
                    font-weight:bold;
                    letter-spacing:0.5px;">
                    %s
                </span>
            </p>
           <div style="
    margin-top:25px;
    padding:15px;
    background:#f9fafb;
    border-radius:8px;
    text-align:center;
    font-size:14px;
    color:#555;">

    %s

</div>
            <div style="
    margin-top:30px;
    text-align:center;">

    <a href="%s"
        target="_blank"
       style="
            background:#111827;
            color:#ffffff;
            text-decoration:none;
            padding:14px 28px;
            border-radius:8px;
            display:inline-block;
            font-weight:bold;
            font-size:15px;">
        View Order →
    </a>

</div>

            <hr>

            <div style="
    text-align:center;
    margin-top:30px;
    color:#6b7280;
    font-size:13px;">

    <p>
        Thank you for shopping with HYNO.
    </p>

    <p>
        Need help? Contact us anytime.
    </p>

    <p>
        © 2026 HYNO Fashion Store
    </p>

</div>

        </div>
</div>
        """.formatted(
                color,      // cho style của <h2>
                icon,       // cho "%s Order Update"
                message,    // cho nội dung <p>
                orderId,
                color,      // màu badge trạng thái
                status.name(),
                timeline,
                orderLink
        );

    sendHtmlEmail(
            to,
            "HYNO - Order Status Updated",
            html
    );
}
}