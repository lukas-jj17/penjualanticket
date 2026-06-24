package com.pbo2.penjualanticket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pbo2.penjualanticket.model.Ticket;
import com.pbo2.penjualanticket.Repository.TicketCategoryRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;

@Controller
public class DashboardController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketCategoryRepository categoryRepo;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            Model model) {

        boolean adaKeyword = StringUtils.hasText(keyword);
        boolean adaKategori = categoryId != null;

        List<Ticket> events;
        if (adaKeyword && adaKategori) {
            events = ticketRepo.findByNameEventContainingIgnoreCaseAndCategoryIdCategory(keyword, categoryId);
        } else if (adaKeyword) {
            events = ticketRepo.findByNameEventContainingIgnoreCase(keyword);
        } else if (adaKategori) {
            events = ticketRepo.findByCategoryIdCategory(categoryId);
        } else {
            events = ticketRepo.findAll();
        }

        model.addAttribute("events", events);
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        return "dashbord";
    }

    @GetMapping("/event/detail/{id}")
    public String detailEvent(@PathVariable Integer id, Model model) {
        Ticket ticket = ticketRepo.findById(id).orElse(null);
        if (ticket == null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("ticket", ticket);
        return "detail-event";
    }
}
