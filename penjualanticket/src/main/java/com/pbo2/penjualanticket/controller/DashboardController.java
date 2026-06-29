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
            @RequestParam(required = false) String city,
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

        // Apply city filter (searching in city, name, or description)
        if (StringUtils.hasText(city)) {
            final String searchCity = city.toLowerCase();
            events = events.stream()
                .filter(e -> (e.getCity() != null && e.getCity().toLowerCase().contains(searchCity)) ||
                             e.getNameEvent().toLowerCase().contains(searchCity) || 
                             (e.getDescription() != null && e.getDescription().toLowerCase().contains(searchCity)))
                .toList();
        }

        // Split trending events: let's pick first 4 events
        List<Ticket> trendingEvents = events.stream().limit(4).toList();

        model.addAttribute("events", events);
        model.addAttribute("trendingEvents", trendingEvents);
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("city", city);
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
