package com.example.application.services;

import com.example.application.entity.*;
import com.example.application.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final AddressRepository addressRepository;
    private final ReservationRepository reservationRepository;

    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Apartment findByUuid(String uuid) {
        return apartmentRepository.findByUuid(uuid);
    }

    @Transactional
    public Apartment save(Apartment apartment, Address address, User landlord) {
        Address savedAddress = addressRepository.save(address);

        apartment.setAddress(savedAddress);
        apartment.setLandlord(landlord);

        if (apartment.getUuid() == null || apartment.getUuid().isEmpty()) {
            apartment.setUuid(UUID.randomUUID().toString());
        }

        return apartmentRepository.save(apartment);
    }

    @Transactional
    public Apartment update(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    @Transactional
    public void delete(Apartment apartment) {
        apartmentRepository.delete(apartment);
    }

    @Transactional(readOnly = true)
    public List<Apartment> findByLandlord(User landlord) {
        return apartmentRepository.findByLandlordId(landlord.getId());
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(Apartment apartment, LocalDate checkIn, LocalDate checkOut) {
        if (apartment == null || checkIn == null || checkOut == null) {
            return false;
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            return false;
        }

        List<Reservation> reservations = reservationRepository.findByApartmentId(apartment.getId());

        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }

            LocalDate reservationStart = reservation.getStartDate();
            LocalDate reservationEnd = reservation.getEndDate();

            boolean overlaps = !(checkOut.isBefore(reservationStart) ||
                    checkOut.isEqual(reservationStart) ||
                    checkIn.isAfter(reservationEnd) ||
                    checkIn.isEqual(reservationEnd));

            if (overlaps) {
                return false;
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public List<Apartment> findAvailableApartments(LocalDate checkIn, LocalDate checkOut) {
        List<Apartment> allApartments = apartmentRepository.findAll();
        return allApartments.stream()
                .filter(apt -> isAvailable(apt, checkIn, checkOut))
                .toList();
    }
}