package com.pbo2.penjualanticket.controller;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pbo2.penjualanticket.model.*;
import com.pbo2.penjualanticket.Repository.*;
import com.pbo2.penjualanticket.service.PenjualanService;
<<<<<<< HEAD
import com.pbo2.penjualanticket.service.PaymentService;
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
<<<<<<< HEAD
    private PenjualanRepository penjualanRepo;

    @Autowired
    private PenjualanService penjualanService;

    @Autowired
    private PaymentService paymentService;

=======
    private PenjualanService penjualanService;

>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
    @GetMapping("/checkout/{id}")
    public String checkoutPage(
            @PathVariable Integer id,
            @RequestParam(value = "error", required = false) String error,
            Model model){

        Ticket ticket = ticketRepo.findById(id).orElse(null);
        if (ticket == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("ticket", ticket);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "checkout";
    }

    @PostMapping("/checkout/save")
    public String saveCheckout(
            @RequestParam Integer idTicket,
            @RequestParam Integer qty,
<<<<<<< HEAD
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
            HttpSession session){

        Customer customer = (Customer) session.getAttribute("user");
        if (customer == null) {
            return "redirect:/login";
        }

        Ticket ticket = ticketRepo.findById(idTicket).orElse(null);
        if (ticket == null) {
            return "redirect:/dashboard";
        }

        if (qty == null || qty < 1) {
            qty = 1;
        }

        try {
            Penjualan penjualan = penjualanService.buatPenjualan(
                    customer, List.of(new CartItem(ticket, qty)));
<<<<<<< HEAD
            
            penjualan.setPaymentMethod(paymentMethod != null && !paymentMethod.trim().isEmpty() ? paymentMethod.toUpperCase() : "VA (HOSTED)");
            penjualan.setPaymentStatus("PENDING");

            // Request payment link from bayar.gg
            Map<String, String> paymentResult = paymentService.createPayment(
                    penjualan.getTotalBayar(),
                    "Pembelian Tiket " + ticket.getNameEvent(),
                    paymentMethod,
                    penjualan.getNoFaktur(),
                    penjualan.getIdPenjualan()
            );

            if (paymentResult != null) {
                penjualan.setPaymentUrl(paymentResult.get("payment_url"));
                penjualan.setReferenceId(paymentResult.get("reference_id"));
                penjualanRepo.save(penjualan);
                
                // Redirect user to bayar.gg payment page
                return "redirect:" + paymentResult.get("payment_url");
            } else {
                penjualanRepo.save(penjualan);
                return "redirect:/nota/" + penjualan.getIdPenjualan();
            }
=======
            return "redirect:/nota/" + penjualan.getIdPenjualan();
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
        } catch (PenjualanService.StockException e) {
            return "redirect:/checkout/" + idTicket + "?error=insufficient_stock";
        }
    }
}
