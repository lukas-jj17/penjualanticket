package com.pbo2.penjualanticket.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${bayar.gg.api-key}")
    private String apiKey;

    @Value("${bayar.gg.checkout-url}")
    private String checkoutUrl;

    @Value("${bayar.gg.webhook-secret}")
    private String webhookSecret;

    @Value("${app.base-url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 
     * 
     * @param amount
     * @param description
     * @param paymentMethod
     * @param invoiceNo
     * @param idPenjualan
     * @return
     */
    public Map<String, String> createPayment(Double amount, String description, String paymentMethod, String invoiceNo,
            Integer idPenjualan) {
        try {
            String callbackUrl = baseUrl + "/api/payment/callback";
            String returnUrl = baseUrl + "/nota/" + idPenjualan;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount.intValue());
            requestBody.put("description", description);
            requestBody.put("payment_url", checkoutUrl);
            requestBody.put("callback_url", callbackUrl);
            requestBody.put("redirect_url", returnUrl);

            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                requestBody.put("payment_method", paymentMethod);
            }

            String jsonPayload = objectMapper.writeValueAsString(requestBody);
            log.info("Sending payment request to bayar.gg: {}", jsonPayload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.bayar.gg/api/create-payment.php"))
                    .header("Content-Type", "application/json")
                    .header("X-API-Key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response from bayar.gg (status {}): {}", response.statusCode(), response.body());

            if (response.statusCode() == 200) {
                Map<String, Object> jsonResponse = objectMapper.readValue(response.body(), Map.class);
                if (Boolean.TRUE.equals(jsonResponse.get("success"))) {
                    Map<String, Object> data = (Map<String, Object>) jsonResponse.get("data");
                    Map<String, String> result = new HashMap<>();
                    result.put("payment_url", String.valueOf(data.get("payment_url")));
                    result.put("reference_id", String.valueOf(data.get("invoice_id")));
                    return result;
                } else {
                    log.error("API bayar.gg returned error status: {}", jsonResponse.get("message"));
                }
            } else {
                log.error("Failed to connect to bayar.gg API. Status code: {}", response.statusCode());
            }

        } catch (Exception e) {
            log.error("Error creating payment link with bayar.gg", e);
        }
        return null;
    }

    public boolean verifySignature(String invoiceId, String status, Object finalAmount, String timestamp,
            String incomingSignature) {
        if (incomingSignature == null || incomingSignature.trim().isEmpty()) {
            return false;
        }
        try {
            String amountStr;
            if (finalAmount instanceof Number) {
                amountStr = String.valueOf(((Number) finalAmount).intValue());
            } else {
                amountStr = String.valueOf(finalAmount);
                if (amountStr.contains(".")) {
                    amountStr = amountStr.substring(0, amountStr.indexOf("."));
                }
            }

            String payload = String.format("%s|%s|%s|%s", invoiceId, status, amountStr, timestamp);
            log.info("Verifying signature. Payload string: '{}'", payload);

            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secretKey);

            byte[] hashBytes = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String computedSignature = hexString.toString();
            log.info("Computed signature: {}, Incoming: {}", computedSignature, incomingSignature);
            return computedSignature.equalsIgnoreCase(incomingSignature);

        } catch (Exception e) {
            log.error("Error verifying signature", e);
            return false;
        }
    }
}
