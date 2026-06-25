package com.pbo2.penjualanticket.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.Ticket;

public interface TicketRepository
extends JpaRepository<Ticket,Integer>{

    List<Ticket> findByNameEventContainingIgnoreCase(String nameEvent);

    List<Ticket> findByCategoryIdCategory(Integer idCategory);

    List<Ticket> findByNameEventContainingIgnoreCaseAndCategoryIdCategory(
            String nameEvent, Integer idCategory);

}
