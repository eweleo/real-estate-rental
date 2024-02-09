package com.example.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Apartment extends AbstractEntity {
    private String uuid;
    private String title;
    @Column(length = 4000)
    private String description;
    private Integer maxPerson;
    private Integer roomsNumber;
    private Double price;
    private String imageURL;
    @ManyToOne
    private Address address;
}
