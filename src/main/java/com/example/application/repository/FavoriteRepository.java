package com.example.application.repository;

import com.example.application.entity.Apartment;
import com.example.application.entity.Favorite;
import com.example.application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserOrderByAddedAtDesc(User user);

    Optional<Favorite> findByUserAndApartment(User user, Apartment apartment);

    boolean existsByUserAndApartment(User user, Apartment apartment);

    void deleteByUserAndApartment(User user, Apartment apartment);
}