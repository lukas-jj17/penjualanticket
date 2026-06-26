package com.pbo2.penjualanticket.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pbo2.penjualanticket.model.CartItem;
import com.pbo2.penjualanticket.model.Customer;
import com.pbo2.penjualanticket.model.Penjualan;
import com.pbo2.penjualanticket.model.Ticket;
import com.pbo2.penjualanticket.Repository.TicketRepository;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;
import com.pbo2.penjualanticket.service.PenjualanService;
import com.pbo2.penjualanticket.service.PaymentService;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

/**
 * Keranjang belanja disimpan di HttpSession dengan atribut "cart"
 * (List<CartItem>). Memungkinkan customer memesan beberapa tiket sekaligus.
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private PenjualanRepository penjualanRepo;

    @Autowired
    private PenjualanService penjualanService;

    @Autowired
    private PaymentService paymentService;

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCart(session);
        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Integer idTicket,
            @RequestParam(required = false) Integer qty,
            HttpSession session) {

        Ticket ticket = ticketRepo.findById(idTicket).orElse(null);
        if (ticket == null) {
            return "redirect:/dashboard";
        }

        int tambah = (qty == null || qty < 1) ? 1 : qty;
        List<CartItem> cart = getCart(session);

        // Jika tiket sudah ada di keranjang, tambah qty-nya
        CartItem existing = cart.stream()
                .filter(i -> i.getTicket().getIdTiket().equals(idTicket))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQty(existing.getQty() + tambah);
        } else {
            cart.add(new CartItem(ticket, tambah));
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Integer idTicket,
            HttpSession session) {

        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getTicket().getIdTiket().equals(idTicket));
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkoutCart(
            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
            HttpSession session,
            Model model) {

        Customer customer = (Customer) session.getAttribute("user");
        if (customer == null) {
            return "redirect:/login";
        }

        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            Penjualan penjualan = penjualanService.buatPenjualan(customer, cart);
            
            penjualan.setPaymentMethod(paymentMethod != null && !paymentMethod.trim().isEmpty() ? paymentMethod.toUpperCase() : "VA (HOSTED)");
            penjualan.setPaymentStatus("PENDING");

            // Request payment link from bayar.gg
            Map<String, String> paymentResult = paymentService.createPayment(
                    penjualan.getTotalBayar(),
                    "Pembelian Tiket dari Keranjang",
                    paymentMethod,
                    penjualan.getNoFaktur(),
                    penjualan.getIdPenjualan()
            );

            session.removeAttribute("cart");

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
        } catch (PenjualanService.StockException e) {
            model.addAttribute("cart", cart);
            model.addAttribute("total",
                    cart.stream().mapToDouble(CartItem::getSubtotal).sum());
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }
}
