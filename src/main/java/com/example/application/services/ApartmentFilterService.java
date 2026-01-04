package com.example.application.services;

import com.example.application.entity.Apartment;
import com.example.application.views.offers.FilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApartmentFilterService {

    private final ApartmentService apartmentService;

    public List<Apartment> filterAndSort(List<Apartment> apartments, FilterCriteria criteria) {
        List<Apartment> filtered = apartments.stream()
                .filter(apt -> matchesCriteria(apt, criteria))
                .collect(Collectors.toList());

        sortApartments(filtered, criteria.getSortBy());
        return filtered;
    }

    private boolean matchesCriteria(Apartment apt, FilterCriteria criteria) {
        if (criteria.getCity() != null && !criteria.getCity().isEmpty()) {
            if (!apt.getAddress().getCity().toLowerCase().contains(criteria.getCity().toLowerCase())) {
                return false;
            }
        }

        if (criteria.getMinPrice() != null && apt.getPrice() < criteria.getMinPrice()) {
            return false;
        }

        if (criteria.getMaxPrice() != null && apt.getPrice() > criteria.getMaxPrice()) {
            return false;
        }

        if (criteria.getRooms() != null && !apt.getRoomsNumber().equals(criteria.getRooms())) {
            return false;
        }

        if (criteria.getGuests() != null && apt.getMaxPerson() < criteria.getGuests()) {
            return false;
        }

        if (criteria.getCheckIn() != null && criteria.getCheckOut() != null) {
            if (!apartmentService.isAvailable(apt, criteria.getCheckIn(), criteria.getCheckOut())) {
                return false;
            }
        }

        return true;
    }

    private void sortApartments(List<Apartment> apartments, String sortBy) {
        if (sortBy == null) return;

        Comparator<Apartment> comparator = switch (sortBy) {
            case "Cena: rosnąco" -> Comparator.comparing(Apartment::getPrice);
            case "Cena: malejąco" -> Comparator.comparing(Apartment::getPrice).reversed();
            case "Najnowsze" -> Comparator.comparing(Apartment::getId).reversed();
            default -> null;
        };

        if (comparator != null) {
            apartments.sort(comparator);
        }
    }
}