package com.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.model.Order;
import com.demo.model.OrderStatus;
import com.demo.repository.OrderRepository;
import com.demo.service.OrderService;
import com.demo.service.PaymentService;
import com.demo.service.VNPayService;
import com.stripe.model.checkout.Session;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletResponse;
import com.demo.service.MailService;
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final OrderRepository orderRepository;
    private final MailService mailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // STRIPE
    @PostMapping("/checkout")
    public String checkout(
            @RequestParam Long orderId,
            @RequestParam Long amount
    ) throws Exception {
        return paymentService.createCheckoutSession(amount, orderId);
    }

    // VNPAY CREATE
    @GetMapping("/vnpay")
    public String createVNPayPayment(
            @RequestParam Long orderId,
            @RequestParam double amount
    ) {
        return vnPayService.createPaymentUrl(orderId, amount);
    }

    // VNPAY RETURN
    @GetMapping("/vnpay-return")
    public void vnPayReturn(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("vnp_TxnRef") Long orderId,
            @RequestParam(value = "vnp_TransactionNo", required = false) String transactionNo,
            @RequestParam(value = "vnp_BankCode", required = false) String bankCode,
            HttpServletResponse response
    ) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String safeFrontendUrl = frontendUrl == null ? "" : frontendUrl.trim();

        if ("00".equals(responseCode)) {

            order.setTransactionNo(transactionNo);
            order.setBankCode(bankCode);
            order.setPaymentTime(LocalDateTime.now());

            // chỉ xác nhận đã thanh toán
            // KHÔNG completed
            order.setStatus(OrderStatus.CONFIRMED);

            orderRepository.save(order);
            mailService.sendPaymentSuccessEmail(
            order.getUser().getEmail(),
            order.getId(),
            order.getTotalPrice(),
            order.getItems()
            );
            response.sendRedirect(safeFrontendUrl + "/payment-success");

        } else {
            response.sendRedirect(safeFrontendUrl + "/payment-failed");
        }
    }

    // STRIPE SUCCESS
    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam("session_id") String sessionId
    ) throws Exception {

        Session session = Session.retrieve(sessionId);

        String orderIdStr = session.getMetadata().get("orderId");

        if (orderIdStr != null) {

            Long orderId = Long.parseLong(orderIdStr);

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(OrderStatus.CONFIRMED);

            orderRepository.save(order);
            mailService.sendPaymentSuccessEmail(
            order.getUser().getEmail(),
            order.getId(),
            order.getTotalPrice(),
             order.getItems()
            );
        }

        return "Payment successful!";
    }
}