package ru.dao.entity.listener;

import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dao.entity.Point;
import ru.service.GeocodingService;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Component
public class PointListener {

    private static GeocodingService geocodingService;

    @Autowired
    public void init(GeocodingService service) {
        PointListener.geocodingService = service;
    }


    @PrePersist
    @PreUpdate
    private void loadCoordinates(Point point) {
        if (!point.getAddress().isEmpty()) {
            try {
                GeocodingResult[] geocodingResults = geocodingService.getAddressCoordinates(point.getAddress());
                if (geocodingResults.length > 0) {
                    point.setX(geocodingResults[0].geometry.location.lat);
                    point.setY(geocodingResults[0].geometry.location.lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

