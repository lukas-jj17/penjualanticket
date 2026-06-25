package com.pbo2.penjualanticket.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pbo2.penjualanticket.model.CartItem;
import com.pbo2.penjualanticket.model.Customer;
import com.pbo2.penjualanticket.model.DetailPenjualan;
import com.pbo2.penjualanticket.model.Penjualan;
import com.pbo2.penjualanticket.model.Ticket;
import com.pbo2.penjualanticket.Repository.DetailPenjualanRepository;
import com.pbo2.penjualanticket.Repository.PenjualanRepository;
import com.pbo2.penjualanticket.Repository.TicketRepository;

@Service
public class PenjualanService {

    private final PenjualanRepository penjualanRepo;
    private final DetailPenjualanRepository detailRepo;
    private final TicketRepository ticketRepo;

    public PenjualanService(PenjualanRepository penjualanRepo,
            DetailPenjualanRepository detailRepo,
            TicketRepository ticketRepo) {
        this.penjualanRepo = penjualanRepo;
        this.detailRepo = detailRepo;
        this.ticketRepo = ticketRepo;
    }

    public static class StockException extends RuntimeException {
        public StockException(String message) {
            super(message);
        }
    }

    /**
     *
     *
     * @return
     * @throws StockException
     */
    @Transactional
    public Penjualan buatPenjualan(Customer customer, List<CartItem> items) {

        if (items == null || items.isEmpty()) {
            throw new StockException("Keranjang kosong.");
        }

        double total = 0.0;
        for (CartItem item : items) {
            Ticket ticket = ticketRepo.findById(item.getTicket().getIdTiket())
                    .orElseThrow(() -> new StockException("Tiket tidak ditemukan."));

            int qty = (item.getQty() == null || item.getQty() < 1) ? 1 : item.getQty();
            int available = ticket.getStock() == null ? 0 : ticket.getStock();

            if (qty > available) {
                throw new StockException("Stok tiket \"" + ticket.getNameEvent()
                        + "\" tidak mencukupi (sisa " + available + ").");
            }
            total += ticket.getPrice() * qty;
        }

        Penjualan penjualan = new Penjualan();
        penjualan.setCustomer(customer);
        penjualan.setTanggal(LocalDate.now());
        penjualan.setNoFaktur("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        penjualan.setTotalBayar(total);
        penjualanRepo.save(penjualan);

        for (CartItem item : items) {
            Ticket ticket = ticketRepo.findById(item.getTicket().getIdTiket()).get();
            int qty = (item.getQty() == null || item.getQty() < 1) ? 1 : item.getQty();
            double subtotal = ticket.getPrice() * qty;

            DetailPenjualan detail = new DetailPenjualan();
            detail.setPenjualan(penjualan);
            detail.setTicket(ticket);
            detail.setQty(qty);
            detail.setSubtotal(subtotal);
            detailRepo.save(detail);

            ticket.setStock(ticket.getStock() - qty);
            ticketRepo.save(ticket);
        }

        return penjualan;
    }
}
