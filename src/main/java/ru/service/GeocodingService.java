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


    /**
     * @param address Physical address
     * @return {@link GeocodingResult} returns what API has to say
     * @throws IOException thrown if there're problems with API, or server-side API has changed
     * @throws InterruptedException thrown when connection to server is lost or is timed out
     * @throws ApiException thrown when there're inherent problems with api
     */
    public GeocodingResult[] getAddressCoordinates(String address) throws IOException, InterruptedException, ApiException {
        GeoApiContext geoApiContext = new GeoApiContext.
                Builder()
                .apiKey(apiKey)
                .maxRetries(3)
                .build();
        return GeocodingApi.geocode(geoApiContext,address).await();
    }
}
