package com.pbo2.penjualanticket.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "penjualan")
public class Penjualan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPenjualan;

    private String noFaktur;

    private LocalDate tanggal;

    private Double totalBayar;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

<<<<<<< HEAD
    private String paymentStatus = "PENDING";

    private String paymentMethod;

    @Column(length = 500)
    private String paymentUrl;

    private String referenceId;

=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
    public Penjualan() {}

    public Integer getIdPenjualan() {
        return idPenjualan;
    }

    public void setIdPenjualan(Integer idPenjualan) {
        this.idPenjualan = idPenjualan;
    }

    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public Double getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(Double totalBayar) {
        this.totalBayar = totalBayar;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
<<<<<<< HEAD

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
}