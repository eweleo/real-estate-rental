package com.example.application.services;

import com.example.application.entity.Reservation;
import com.example.application.entity.User;
import com.example.application.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository repository;

    @Transactional
    public void createReservation(Reservation reservation) {
        repository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Optional<Reservation> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByLandlordId(Long landlordId) {
        return repository.findByLandlordId(landlordId);
    }

    @Transactional
    public List<Reservation> findByUserId(Long userId){
        return repository.findByUserId(userId);
    }

    @Transactional
    public Reservation update(Reservation reservation) {
        return repository.save(reservation);
    }

}
