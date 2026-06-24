package com.pbo2.penjualanticket.controller;

import java.io.IOException;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pbo2.penjualanticket.config.WebConfig;
import com.pbo2.penjualanticket.model.Ticket;
import com.pbo2.penjualanticket.model.TicketCategory;
import com.pbo2.penjualanticket.Repository.TicketCategoryRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;

@Controller
@RequestMapping("/admin/event")
public class EventController {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketCategoryRepository categoryRepo;

    @GetMapping
    public String eventPage(Model model){
        model.addAttribute("events", ticketRepo.findAll());
        return "admin/event";
    }

    @GetMapping("/add")
    public String addPage(Model model){
        model.addAttribute("event", new Ticket());
        model.addAttribute("categories", categoryRepo.findAll());
        return "admin/event-add";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model){
        Ticket ticket = ticketRepo.findById(id).orElse(null);
        if (ticket == null) {
            return "redirect:/admin/event";
        }
        model.addAttribute("event", ticket);
        model.addAttribute("categories", categoryRepo.findAll());
        return "admin/event-add";
    }

    @PostMapping("/save")
    public String saveEvent(
            Ticket ticket,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam("gambar") MultipartFile file)
            throws IOException {

        // Saat edit: pertahankan foto lama jika tidak meng-upload gambar baru
        if (ticket.getIdTiket() != null) {
            Ticket existing = ticketRepo.findById(ticket.getIdTiket()).orElse(null);
            if (existing != null && (file == null || file.isEmpty())) {
                ticket.setPhoto(existing.getPhoto());
            }
        }

        // Bind kategori dari id (form mengirim categoryId, bukan objek)
        if (categoryId != null) {
            TicketCategory category = categoryRepo.findById(categoryId).orElse(null);
            ticket.setCategory(category);
        }

        // Simpan gambar ke folder runtime ./uploads (di-serve via WebConfig)
        if (file != null && !file.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(WebConfig.UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            ticket.setPhoto(fileName);
        }

        ticketRepo.save(ticket);
        return "redirect:/admin/event";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Integer id){
        ticketRepo.deleteById(id);
        return "redirect:/admin/event";
    }
}
