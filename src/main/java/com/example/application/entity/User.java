package com.example.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.helger.commons.annotation.LazilyInitialized;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "application_user")
public class User extends AbstractEntity {
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
    @ElementCollection(fetch = FetchType.EAGER)  // EAGER loading
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Company company;

    public boolean isLandlord(){
        return roles.contains(Role.LANDLORD);
    }
}
