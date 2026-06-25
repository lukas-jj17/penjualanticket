package com.pbo2.penjualanticket.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.TicketCategory;

public interface TicketCategoryRepository
extends JpaRepository<TicketCategory,Integer>{

}