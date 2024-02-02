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
public class Apartment extends  AbstractEntity{
    private UUID uuid = UUID.randomUUID();
    private String title;
    private String description;
    private Float area;
    private Integer roomsNumber;
    private Integer floor;
    private Float price;
    private String imageURL;
    @OneToOne
    private Address address;
    @ManyToOne
    private User owner;
}
