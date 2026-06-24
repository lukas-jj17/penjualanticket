package com.pbo2.penjualanticket.model;

import java.io.Serializable;

/**
 * Item keranjang belanja. Bukan entity JPA — hanya disimpan di HttpSession.
 */
public class CartItem implements Serializable {

    private Ticket ticket;
    private Integer qty;

    public CartItem() {}

    public CartItem(Ticket ticket, Integer qty) {
        this.ticket = ticket;
        this.qty = qty;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getSubtotal() {
        if (ticket == null || ticket.getPrice() == null || qty == null) {
            return 0.0;
        }
        return ticket.getPrice() * qty;
    }
}
