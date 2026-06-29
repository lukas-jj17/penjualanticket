package com.pbo2.penjualanticket.model;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTiket;

    private String nameEvent;

    private Double price;

    private Integer stock;

    @Column(length = 1000)
    private String description;

    private String photo;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private TicketCategory category;

    private String city;

    private String eventDate;

    private String eventTime;
    private String location;
    private String startDay;
    private String endDay;
    private Boolean hasCustomCategories;
    private Boolean hasDayPrices;
    private Double vipPrice;
    private Double regulerPrice;
    private Double earlyBirdPrice;
    private Double weekdayPrice;
    private Double weekendPrice;

    public Ticket() {}

    public Integer getIdTiket() {
        return idTiket;
    }

    public void setIdTiket(Integer idTiket) {
        this.idTiket = idTiket;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getFormattedEventDate() {
        if (eventDate == null || eventDate.trim().isEmpty()) {
            return "";
        }
        try {
            LocalDate ld = LocalDate.parse(eventDate);
            return ld.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy", new java.util.Locale("id")));
        } catch (Exception e) {
            // Jika bukan format YYYY-MM-DD (misalnya format lama text), kembalikan apa adanya
            return eventDate;
        }
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public Boolean getHasCustomCategories() {
        return hasCustomCategories;
    }

    public void setHasCustomCategories(Boolean hasCustomCategories) {
        this.hasCustomCategories = hasCustomCategories;
    }

    public Boolean getHasDayPrices() {
        return hasDayPrices;
    }

    public void setHasDayPrices(Boolean hasDayPrices) {
        this.hasDayPrices = hasDayPrices;
    }

    public Double getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(Double vipPrice) {
        this.vipPrice = vipPrice;
    }

    public Double getRegulerPrice() {
        return regulerPrice;
    }

    public void setRegulerPrice(Double regulerPrice) {
        this.regulerPrice = regulerPrice;
    }

    public Double getEarlyBirdPrice() {
        return earlyBirdPrice;
    }

    public void setEarlyBirdPrice(Double earlyBirdPrice) {
        this.earlyBirdPrice = earlyBirdPrice;
    }

    public Double getWeekdayPrice() {
        return weekdayPrice;
    }

    public void setWeekdayPrice(Double weekdayPrice) {
        this.weekdayPrice = weekdayPrice;
    }

    public Double getWeekendPrice() {
        return weekendPrice;
    }

    public void setWeekendPrice(Double weekendPrice) {
        this.weekendPrice = weekendPrice;
    }
}