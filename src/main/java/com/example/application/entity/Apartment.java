package com.example.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    @ManyToOne
    @JoinColumn(name = "landlord_Id")
    private User landlord;
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("uploadOrder ASC")
    private List<ApartmentImage> images = new ArrayList<>();

    public void addImage(ApartmentImage image) {
        images.add(image);
        image.setApartment(this);
        image.setUploadOrder(images.size() - 1);
    }

    public void removeImage(ApartmentImage image) {
        images.remove(image);
        image.setApartment(null);
    }

    public ApartmentImage getMainImage() {
        return images.stream()
                .filter(ApartmentImage::isMainImage)
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }

    public void setMainImage(ApartmentImage newMainImage) {
        images.forEach(img -> img.setMainImage(false));
        if (newMainImage != null && images.contains(newMainImage)) {
            newMainImage.setMainImage(true);
        }
    }
}
