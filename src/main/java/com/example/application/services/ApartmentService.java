package com.example.application.services;

import com.example.application.entity.Apartment;
import com.example.application.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }

    public Apartment getByUuid(String uuid) {
        return apartmentRepository.findByUuid(uuid);
    }
}
