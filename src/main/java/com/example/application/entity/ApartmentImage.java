package com.example.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ApartmentImage extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;
    @Lob
    @Column(length = 10000000, nullable = false)
    private byte[] imageData;
    private String fileName;
    private String contentType;
    private boolean isMainImage = false;
    @Column(name = "upload_order")
    private Integer uploadOrder;
}
