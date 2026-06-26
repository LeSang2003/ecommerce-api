package com.demo.service;

import com.demo.config.VNPayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;

    public String createPaymentUrl(
            Long orderId,
            double amount
    ) {

        try {

            String vnp_Version = "2.1.0";

            String vnp_Command = "pay";

            String orderType = "other";

            long total =
                    (long) (amount * 100);

            String vnp_TxnRef =
                    String.valueOf(orderId);

            String vnp_IpAddr =
                    "127.0.0.1";

            String vnp_TmnCode =
                    vnPayConfig.getTmnCode();

            Map<String, String> vnp_Params =
                    new HashMap<>();

            vnp_Params.put(
                    "vnp_Version",
                    vnp_Version
            );

            vnp_Params.put(
                    "vnp_Command",
                    vnp_Command
            );

            vnp_Params.put(
                    "vnp_TmnCode",
                    vnp_TmnCode
            );

            vnp_Params.put(
                    "vnp_Amount",
                    String.valueOf(total)
            );

            vnp_Params.put(
                    "vnp_CurrCode",
                    "VND"
            );

            vnp_Params.put(
                    "vnp_TxnRef",
                    vnp_TxnRef
            );

            vnp_Params.put(
                    "vnp_OrderInfo",
                    "Payment order " + orderId
            );

            vnp_Params.put(
                    "vnp_OrderType",
                    orderType
            );

            vnp_Params.put(
                    "vnp_Locale",
                    "vn"
            );

            vnp_Params.put(
                    "vnp_ReturnUrl",
                    vnPayConfig.getReturnUrl()
            );

            vnp_Params.put(
                    "vnp_IpAddr",
                    vnp_IpAddr
            );

            Calendar cld =
                    Calendar.getInstance(
                            TimeZone.getTimeZone("Etc/GMT+7")
                    );

            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "yyyyMMddHHmmss"
                    );

            String vnp_CreateDate =
                    formatter.format(cld.getTime());

            vnp_Params.put(
                    "vnp_CreateDate",
                    vnp_CreateDate
            );

            List<String> fieldNames =
                    new ArrayList<>(vnp_Params.keySet());

            Collections.sort(fieldNames);

            StringBuilder hashData =
                    new StringBuilder();

            StringBuilder query =
                    new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();

while (itr.hasNext()) {

    String fieldName = itr.next();

    String fieldValue =
            vnp_Params.get(fieldName);

    if (
            fieldValue != null
                    && !fieldValue.isEmpty()
    ) {

        hashData.append(fieldName);

        hashData.append('=');

        hashData.append(
                URLEncoder.encode(
                        fieldValue,
                       StandardCharsets.UTF_8.toString()
                )
        );

        query.append(
                URLEncoder.encode(
                        fieldName,
                         StandardCharsets.UTF_8.toString()
                )
        );

        query.append('=');

        query.append(
                URLEncoder.encode(
                        fieldValue,
                       StandardCharsets.UTF_8.toString()
                )
        );

        if (itr.hasNext()) {

            query.append('&');

            hashData.append('&');
        }
                }   
        }


            String secureHash =
                    hmacSHA512(
                            vnPayConfig.getHashSecret(),
                            hashData.toString()
                    );

            String queryUrl =
                        query.toString();

                        queryUrl +=
                                "&vnp_SecureHash=" + secureHash;

                        String paymentUrl =
                        vnPayConfig.getPayUrl()
                                + "?"
                                + queryUrl;

                return paymentUrl;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Create VNPay failed"
            );
        }
    }

    private String hmacSHA512(
            String key,
            String data
    ) throws Exception {

        Mac hmac512 =
                Mac.getInstance("HmacSHA512");

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        key.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA512"
                );

        hmac512.init(secretKeySpec);

        byte[] bytes =
                hmac512.doFinal(
                        data.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        StringBuilder hash =
                new StringBuilder();

        for (byte b : bytes) {

            hash.append(
                    String.format("%02x", b)
            );
        }

        return hash.toString();
    }
}