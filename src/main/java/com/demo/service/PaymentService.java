package com.demo.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public String createCheckoutSession(Long amount, Long orderId) throws Exception {

    SessionCreateParams params =
            SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://${import.meta.env.VITE_API_HOST}/api/payment/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:3000/cancel")
                    .putMetadata("orderId", String.valueOf(orderId))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amount)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Order Payment")
                                                                    .build())
                                                    .build())
                                    .build())
                    .build();

    Session session = Session.create(params);

    return session.getUrl();
 }
}