package com.pbo2.penjualanticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Konfigurasi bean umum aplikasi.
 * BCryptPasswordEncoder dipakai untuk meng-hash & memverifikasi password
 * (login tetap berbasis HttpSession, tanpa mengaktifkan seluruh Spring Security).
 */
@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
