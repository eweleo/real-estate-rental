package com.example.application.services;
import com.example.application.entity.Apartment;
import com.example.application.entity.Favorite;
import com.example.application.entity.User;
import com.example.application.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional(readOnly = true)
    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUserOrderByAddedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(User user, Apartment apartment) {
        return favoriteRepository.existsByUserAndApartment(user, apartment);
    }

    @Transactional
    public void addToFavorites(User user, Apartment apartment) {
        if (!isFavorite(user, apartment)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setApartment(apartment);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFromFavorites(User user, Apartment apartment) {
        favoriteRepository.deleteByUserAndApartment(user, apartment);
    }

    @Transactional
    public void toggleFavorite(User user, Apartment apartment) {
        if (isFavorite(user, apartment)) {
            removeFromFavorites(user, apartment);
        } else {
            addToFavorites(user, apartment);
        }
    }
}