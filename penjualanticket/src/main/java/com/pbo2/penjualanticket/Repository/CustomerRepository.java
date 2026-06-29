package com.pbo2.penjualanticket.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pbo2.penjualanticket.model.Customer;

public interface CustomerRepository
extends JpaRepository<Customer, Integer>{

    Customer findByEmail(String email);
    Customer findFirstByEmail(String email);

}
