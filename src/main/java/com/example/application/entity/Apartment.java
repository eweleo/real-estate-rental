package com.example.application.entity;

import com.example.application.model.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Apartment {
    @Id
    private UUID id = UUID.randomUUID();
    private List<Category> category;
    private String description;
    private Float area;
    private Integer roomsNumber;
    private Integer floor;
    private Float price;
    @Lob
    private byte[] image;
    @OneToOne
    private Address address;
    @ManyToOne
    private Customer owner;
}
