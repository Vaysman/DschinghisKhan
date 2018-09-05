package ru.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class GeocodingService {
    @Value("${google-maps-key}")
    private String apiKey;


    public GeocodingResult[] getAddressCoordinates(String address) throws IOException, InterruptedException, ApiException {
        GeoApiContext geoApiContext = new GeoApiContext.
                Builder()
                .apiKey(apiKey)
                .maxRetries(3)
                .build();
        return GeocodingApi.geocode(geoApiContext,address).await();
    }
}
