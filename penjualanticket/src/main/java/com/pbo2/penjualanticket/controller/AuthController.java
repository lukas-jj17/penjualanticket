package com.pbo2.penjualanticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pbo2.penjualanticket.model.Customer;
import com.pbo2.penjualanticket.Repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    CustomerRepository customerRepo;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }   

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String saveRegister(Customer customer, Model model) {

        Customer existing = customerRepo.findFirstByEmail(customer.getEmail());
        if (existing != null) {
            model.addAttribute("error", "Email sudah terdaftar!");
            model.addAttribute("customer", customer);
            return "register";
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepo.save(customer);

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Customer customer = customerRepo.findFirstByEmail(email);

        if(customer != null &&
           passwordEncoder.matches(password, customer.getPassword())) {

            session.setAttribute("user", customer);

            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Email atau Password salah");

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

}
