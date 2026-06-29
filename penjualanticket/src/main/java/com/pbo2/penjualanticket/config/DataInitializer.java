package com.pbo2.penjualanticket.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pbo2.penjualanticket.model.Admin;
import com.pbo2.penjualanticket.model.Ticket;
import com.pbo2.penjualanticket.model.TicketCategory;
import com.pbo2.penjualanticket.Repository.AdminRepository;
import com.pbo2.penjualanticket.Repository.TicketCategoryRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;

/**
 * Mengisi data awal saat aplikasi pertama kali dijalankan (jika tabel masih kosong):
 * 1 admin default, beberapa kategori, dan beberapa contoh event.
 * Tujuannya agar aplikasi langsung bisa dipakai/demo tanpa setup manual.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final TicketCategoryRepository categoryRepo;
    private final TicketRepository ticketRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepo,
                           TicketCategoryRepository categoryRepo,
                           TicketRepository ticketRepo,
                           BCryptPasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.categoryRepo = categoryRepo;
        this.ticketRepo = ticketRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Admin default
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setName("Administrator");
            admin.setEmail("admin@ticket.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNoTlp("08123456789");
            adminRepo.save(admin);
        }

        // Kategori + contoh event
        String[] defaultCategories = {
            "Atraksi & Taman Hiburan",
            "Balapan & Ketahanan",
            "Festival & Pameran",
            "Komunitas & Perkumpulan",
            "Konferensi & Seminar",
            "Konser",
            "Pertunjukan & Penampilan",
            "Sesi Olahraga",
            "Tur & Perjalanan",
            "Turnamen & Kompetisi",
            "Workshop & Pelatihan"
        };

        for (String catName : defaultCategories) {
            boolean exists = categoryRepo.findAll().stream()
                    .anyMatch(c -> c.getNameCategory().equalsIgnoreCase(catName));
            if (!exists) {
                simpanKategori(catName);
            }
        }

        if (ticketRepo.count() == 0) {
            TicketCategory konser = categoryRepo.findAll().stream()
                    .filter(c -> c.getNameCategory().equalsIgnoreCase("Konser"))
                    .findFirst().orElse(null);
            TicketCategory olahraga = categoryRepo.findAll().stream()
                    .filter(c -> c.getNameCategory().equalsIgnoreCase("Sesi Olahraga"))
                    .findFirst().orElse(null);

            if (konser != null) {
                simpanTicket("Konser Musik Akbar 2026", 250000.0, 100,
                        "Konser musik terbesar tahun ini dengan deretan musisi ternama.", konser);
            }
            if (olahraga != null) {
                simpanTicket("Final Liga Nasional", 150000.0, 200,
                        "Saksikan laga final memperebutkan gelar juara.", olahraga);
            }
        }
    }

    private TicketCategory simpanKategori(String nama) {
        TicketCategory c = new TicketCategory();
        c.setNameCategory(nama);
        return categoryRepo.save(c);
    }

    private void simpanTicket(String nama, Double harga, Integer stok,
                              String deskripsi, TicketCategory kategori) {
        Ticket t = new Ticket();
        t.setNameEvent(nama);
        t.setPrice(harga);
        t.setStock(stok);
        t.setDescription(deskripsi);
        t.setCategory(kategori);
        ticketRepo.save(t);
    }
}
