package com.example.application.services;

import com.example.application.entity.Reservation;
import com.example.application.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository repository;

    public void save(Reservation reservation) {
        repository.save(reservation);
    }

    public Double getRateCurrency(String currency){
        WebClient client = WebClient.create();

        String response = client.get()
                .uri("http://api.nbp.pl/api/exchangerates/rates/A/" + currency)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject json = new JSONObject(response);
        JSONArray jsonArray = json.getJSONArray("rates");
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        return jsonObject.getDouble("mid");
    }
}
