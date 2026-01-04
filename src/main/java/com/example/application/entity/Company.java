package com.example.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String companyName;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String nip;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
