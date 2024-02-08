package com.example.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Reservation extends AbstractEntity{
    @ManyToOne
    private User user;
    @ManyToOne
    private Apartment apartment;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Double fullPrice;
}
