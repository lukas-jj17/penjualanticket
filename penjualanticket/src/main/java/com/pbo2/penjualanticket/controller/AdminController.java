package com.pbo2.penjualanticket.controller;

<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pbo2.penjualanticket.Repository.CustomerRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;
import com.pbo2.penjualanticket.model.Penjualan;
=======

import com.pbo2.penjualanticket.Repository.CustomerRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822

@Controller
public class AdminController {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    TicketRepository ticketRepo;

<<<<<<< HEAD
    @Autowired
    PenjualanRepository penjualanRepo;

=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "totalCustomer",
                customerRepo.count());

        model.addAttribute(
                "totalEvent",
                ticketRepo.count());

<<<<<<< HEAD
        List<Penjualan> suksesList = penjualanRepo.findAll().stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getPaymentStatus()))
                .toList();
        double totalPendapatan = suksesList.stream().mapToDouble(Penjualan::getTotalBayar).sum();
        long totalTransaksi = penjualanRepo.count();

        model.addAttribute("totalPendapatan", totalPendapatan);
        model.addAttribute("totalTransaksi", totalTransaksi);

        return "admin/dashbord";
    }

    @GetMapping("/admin/riwayat")
    public String adminRiwayat(Model model) {
        List<Penjualan> listPenjualan = penjualanRepo.findAll();
        // Urutkan transaksi terbaru di atas
        listPenjualan.sort((a, b) -> b.getIdPenjualan().compareTo(a.getIdPenjualan()));
        model.addAttribute("riwayat", listPenjualan);
        return "admin/riwayat";
    }

    @PostMapping("/admin/riwayat/update-status")
    public String updateStatus(@RequestParam Integer idPenjualan, @RequestParam String status) {
        Penjualan penjualan = penjualanRepo.findById(idPenjualan).orElse(null);
        if (penjualan != null) {
            penjualan.setPaymentStatus(status.toUpperCase());
            penjualanRepo.save(penjualan);
        }
        return "redirect:/admin/riwayat";
    }

=======
        return "admin/dashbord";
    }

>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
}