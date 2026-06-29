package com.pbo2.penjualanticket.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.Admin;

public interface AdminRepository
extends JpaRepository<Admin,Integer>{

    Admin findByEmail(String email);
    Admin findFirstByEmail(String email);

}
