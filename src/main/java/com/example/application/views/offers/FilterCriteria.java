package com.example.application.views.offers;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FilterCriteria {
    private String city;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer rooms;
    private Integer guests;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String sortBy = "Najnowsze";

    public void clear() {
        city = null;
        minPrice = null;
        maxPrice = null;
        rooms = null;
        guests = null;
        checkIn = null;
        checkOut = null;
        sortBy = "Najnowsze";
    }
}