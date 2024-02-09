package com.example.application.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Address extends AbstractEntity {
    private String street;
    private String streetNumber;
    private String apartmentNumber;
    private String zipCode;
    private String City;
}
