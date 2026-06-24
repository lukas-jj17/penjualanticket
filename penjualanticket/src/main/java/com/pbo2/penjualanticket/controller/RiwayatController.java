package com.pbo2.penjualanticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pbo2.penjualanticket.model.Customer;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class RiwayatController {

    @Autowired
    private PenjualanRepository penjualanRepo;

    @GetMapping("/riwayat")
    public String riwayat(
            HttpSession session,
            Model model){

        Customer customer =
                (Customer)
                session.getAttribute("user");

        model.addAttribute(
                "riwayat",
                penjualanRepo
                .findByCustomerIdCustomer(
                customer.getIdCustomer()));

        return "riwayat";
    }
}