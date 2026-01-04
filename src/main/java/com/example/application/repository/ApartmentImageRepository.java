package com.example.application.repository;

import com.example.application.entity.ApartmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentImageRepository extends JpaRepository<ApartmentImage,Long> {
    List<ApartmentImage> findByApartmentIdOrderByUploadOrderAsc(Long apartmentId);
}
