package com.example.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {
    private String username;
    @JsonIgnore
    private String hashedPassword;
    @Lob
    @Column(length = 1000000)
    private byte[] profilePicture;
    private String FirstName;
    private String LastName;
    private String email;
    private String telephoneNumber;
    @OneToMany
    private List<Apartment> observed;
}
