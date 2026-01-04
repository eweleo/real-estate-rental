package com.example.application.repository;

import com.example.application.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUuid(String uuid);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByApartmentId(Long apartmentId);

    @Query("SELECT r FROM Reservation r WHERE r.apartment.id = :apartmentId " +
            "AND r.status != 'CANCELLED' " +
            "AND ((r.startDate <= :checkOut AND r.endDate >= :checkIn))")
    List<Reservation> findConflictingReservations(
            @Param("apartmentId") Long apartmentId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    @Query("SELECT r FROM Reservation r WHERE r.apartment.landlord.id = :landlordId")
    List<Reservation> findByLandlordId(@Param("landlordId") Long landlordId);
}
