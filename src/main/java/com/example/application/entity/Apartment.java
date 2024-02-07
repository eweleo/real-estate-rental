package com.example.application.entity;

import com.example.application.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
public class Apartment extends  AbstractEntity{
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
