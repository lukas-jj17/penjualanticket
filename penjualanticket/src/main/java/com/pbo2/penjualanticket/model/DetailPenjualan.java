package com.pbo2.penjualanticket.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detail_penjualan")
public class DetailPenjualan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetails;

    private Integer qty;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "id_penjualan")
    private Penjualan penjualan;

    @ManyToOne
    @JoinColumn(name = "id_tiket")
    private Ticket ticket;

    private String selectedCategory;

    public DetailPenjualan() {}

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Integer getIdDetails() {
        return idDetails;
    }

    public void setIdDetails(Integer idDetails) {
        this.idDetails = idDetails;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Penjualan getPenjualan() {
        return penjualan;
    }

    public void setPenjualan(Penjualan penjualan) {
        this.penjualan = penjualan;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}