package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    private final RestTemplate rest = new RestTemplate();
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public LatLng geocode(String endereco) {
        URI uri = UriComponentsBuilder
                .fromUriString(NOMINATIM_URL)
                .queryParam("q", endereco)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .queryParam("countrycodes", "br") 
                .build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "doacaolocal-backend/1.0 (seu-email@dominio.com)");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = rest.exchange(
                uri,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        List<Map<String, Object>> results = response.getBody();
        if (results != null && !results.isEmpty()) {
            Map<String, Object> first = results.get(0);
            double lat = Double.parseDouble((String) first.get("lat"));
            double lon = Double.parseDouble((String) first.get("lon"));
            return new LatLng(lat, lon);
        }
        throw new RuntimeException("Não foi possível geocodificar: " + endereco);
    }

    public record LatLng(double lat, double lng) {
    }
}