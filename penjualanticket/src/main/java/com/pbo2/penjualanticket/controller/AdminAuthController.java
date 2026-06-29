package com.pbo2.penjualanticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pbo2.penjualanticket.model.Admin;
import com.pbo2.penjualanticket.Repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model){

        Admin admin =
                adminRepo.findFirstByEmail(email);

        if(admin != null &&
           passwordEncoder.matches(password, admin.getPassword())){

            session.setAttribute("admin", admin);

            return "redirect:/admin/dashboard";
        }

        model.addAttribute(
                "error",
                "Email atau Password Salah");

        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session){

        session.invalidate();

        return "redirect:/admin/login";
    }
}