package com.pbo2.penjualanticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pbo2.penjualanticket.Repository.CustomerRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;

@Controller
public class AdminController {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    TicketRepository ticketRepo;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        model.addAttribute(
                "totalCustomer",
                customerRepo.count());

        model.addAttribute(
                "totalEvent",
                ticketRepo.count());

        return "admin/dashbord";
    }

}