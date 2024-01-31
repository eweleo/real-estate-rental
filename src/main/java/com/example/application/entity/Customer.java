package com.example.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String Firstname;
    private String Lastname;
    private String email;
    private String telephoneNumber;
    @OneToMany(mappedBy = "owner")
    private List<Apartment> offers;
    @OneToMany
    private List<Apartment> observed;
}
