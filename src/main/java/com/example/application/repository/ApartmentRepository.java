package com.example.application.repository;

import com.example.application.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    @Query("SELECT DISTINCT a FROM Apartment a LEFT JOIN FETCH a.images LEFT JOIN FETCH a.address LEFT JOIN FETCH a.landlord WHERE a.landlord.id = :landlordId")
    List<Apartment> findByLanIdWithImages(@Param("landlordId") Long landlordId);

    @Query("SELECT DISTINCT a FROM Apartment a LEFT JOIN FETCH a.images LEFT JOIN FETCH a.address LEFT JOIN FETCH a.landlord")
    List<Apartment> findAllWithImages();

    @Query("SELECT DISTINCT a FROM Apartment a LEFT JOIN FETCH a.images LEFT JOIN FETCH a.address LEFT JOIN FETCH a.landlord WHERE a.uuid = :uuid")
    Apartment findByUuidWithImages(@Param("uuid") String uuid);

    Apartment findByUuid(String uuid);

    List<Apartment> findByLandlordId(Long landlordId);
}
