package com.pbo2.penjualanticket.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pbo2.penjualanticket.Repository.CustomerRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;
import com.pbo2.penjualanticket.Repository.DetailPenjualanRepository;
import com.pbo2.penjualanticket.model.Penjualan;
import com.pbo2.penjualanticket.model.Customer;
import com.pbo2.penjualanticket.model.DetailPenjualan;

@Controller
public class AdminController {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    TicketRepository ticketRepo;

    @Autowired
    PenjualanRepository penjualanRepo;

    @Autowired
    DetailPenjualanRepository detailPenjualanRepo;
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "totalCustomer",
                customerRepo.count());

        model.addAttribute(
                "totalEvent",
                ticketRepo.count());

        List<Penjualan> suksesList = penjualanRepo.findAll().stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getPaymentStatus()))
                .toList();
        double totalPendapatan = suksesList.stream().mapToDouble(Penjualan::getTotalBayar).sum();
        long totalTransaksi = penjualanRepo.count();

        model.addAttribute("totalPendapatan", totalPendapatan);
        model.addAttribute("totalTransaksi", totalTransaksi);

        return "admin/dashbord";
    }

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

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

    @GetMapping("/admin/customer")
    public String adminCustomer(Model model) {
        model.addAttribute("customers", customerRepo.findAll());
        return "admin/customer";
    }

    @GetMapping("/admin/customer/edit/{id}")
    public String editCustomerForm(@PathVariable Integer id, Model model) {
        Customer customer = customerRepo.findById(id).orElse(null);
        if (customer != null) {
            model.addAttribute("customer", customer);
            return "admin/customer-edit";
        }
        return "redirect:/admin/customer";
    }

    @PostMapping("/admin/customer/save")
    public String saveCustomer(Customer customer) {
        Customer existing = customerRepo.findById(customer.getIdCustomer()).orElse(null);
        if (existing != null) {
            existing.setName(customer.getName());
            existing.setEmail(customer.getEmail());
            existing.setNoTlp(customer.getNoTlp());
            if (customer.getPassword() != null && !customer.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(customer.getPassword()));
            }
            customerRepo.save(existing);
        }
        return "redirect:/admin/customer";
    }

    @GetMapping("/admin/customer/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        List<Penjualan> customerSales = penjualanRepo.findAll().stream()
                .filter(p -> p.getCustomer() != null && p.getCustomer().getIdCustomer().equals(id))
                .toList();

        for (Penjualan p : customerSales) {
            List<DetailPenjualan> details = detailPenjualanRepo.findAll().stream()
                    .filter(d -> d.getPenjualan() != null && d.getPenjualan().getIdPenjualan().equals(p.getIdPenjualan()))
                    .toList();
            detailPenjualanRepo.deleteAll(details);
            penjualanRepo.delete(p);
        }

        customerRepo.deleteById(id);
        return "redirect:/admin/customer";
    }
}