package com.example.application.security;

import com.example.application.entity.Reservation;
import com.example.application.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository repository;

    public void save(Reservation reservation) {
        repository.save(reservation);
    }
}
