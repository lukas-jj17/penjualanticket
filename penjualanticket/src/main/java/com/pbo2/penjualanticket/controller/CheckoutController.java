package com.pbo2.penjualanticket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pbo2.penjualanticket.model.*;
import com.pbo2.penjualanticket.Repository.*;
import com.pbo2.penjualanticket.service.PenjualanService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private PenjualanService penjualanService;

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
            return "redirect:/nota/" + penjualan.getIdPenjualan();
        } catch (PenjualanService.StockException e) {
            return "redirect:/checkout/" + idTicket + "?error=insufficient_stock";
        }
    }
}
