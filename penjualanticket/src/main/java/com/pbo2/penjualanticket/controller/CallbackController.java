package com.pbo2.penjualanticket.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pbo2.penjualanticket.model.Penjualan;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;
import com.pbo2.penjualanticket.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payment")
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);

    @Autowired
    private PenjualanRepository penjualanRepo;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(
            @RequestHeader(value = "X-Webhook-Signature", required = false) String headerSignature,
            @RequestBody Map<String, Object> payload) {
        log.info("Received callback from bayar.gg: {}, Header Signature: {}", payload, headerSignature);

        try {
            String invoiceId = String.valueOf(payload.get("invoice_id"));
            String status = String.valueOf(payload.get("status"));
            Object finalAmount = payload.get("final_amount");
            String paidVia = String.valueOf(payload.get("paid_via"));
            String timestamp = String.valueOf(payload.get("timestamp"));
            
            String signature = headerSignature;
            if (signature == null || signature.trim().isEmpty() || "null".equalsIgnoreCase(signature)) {
                signature = String.valueOf(payload.get("signature"));
            }

            boolean isValid = paymentService.verifySignature(invoiceId, status, finalAmount, timestamp, signature);
            if (!isValid) {
                log.warn("Invalid signature from callback!");
                return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid signature"));
            }

            // Temukan transaksi berdasarkan referenceId (yang dikirim sebagai invoiceId di callback bayar.gg)
            Penjualan penjualan = penjualanRepo.findByReferenceId(invoiceId).orElse(null);
            if (penjualan == null) {
                log.warn("Transaction not found for reference ID: {}", invoiceId);
                return ResponseEntity.status(404).body(Map.of("status", "error", "message", "Transaction not found"));
            }

            if ("success".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)) {
                penjualan.setPaymentStatus("SUCCESS");
                if (paidVia != null && !paidVia.trim().isEmpty() && !"null".equalsIgnoreCase(paidVia)) {
                    penjualan.setPaymentMethod(paidVia.toUpperCase());
                }
                penjualanRepo.save(penjualan);
                log.info("Transaction {} marked as paid via {}", penjualan.getNoFaktur(), paidVia);
            } else {
                log.info("Callback status was {}, not updated to paid", status);
            }

            return ResponseEntity.ok(Map.of("status", "success", "message", "Callback processed successfully"));

        } catch (Exception e) {
            log.error("Error processing callback", e);
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Error processing callback"));
        }
    }
}
