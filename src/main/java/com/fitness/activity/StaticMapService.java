package com.fitness.activity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fitness.activity.dto.RoutePoint;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaticMapService {

    @Value("${google.maps.api-key}")
    private String GOOGLE_MAPS_API_KEY;

    public byte[] generateMapImage(List<RoutePoint> routeData) {
        if (routeData == null || routeData.size() < 2) {
            return new byte[0]; // Need at least 2 points to draw a line
        }

        int MAX_POINTS = 100;

        // 2. Dynamically calculate the step size.
        // If route has 50 points, step is 1. If it has 1000 points, step is 10.
        int step = Math.max(1, routeData.size() / MAX_POINTS);

        StringBuilder pathBuilder = new StringBuilder();

        // 3. Extract the points
        for (int i = 0; i < routeData.size(); i += step) {
            RoutePoint p = routeData.get(i);
            pathBuilder.append(p.lat()).append(",").append(p.lng()).append("|");
        }

        // 4. Always ensure the absolute final coordinate is included so the route
        // doesn't stop short
        if ((routeData.size() - 1) % step != 0) {
            RoutePoint p = routeData.get(routeData.size() - 1);
            pathBuilder.append(p.lat()).append(",").append(p.lng()).append("|");
        }

        // Remove the trailing "|"
        if (pathBuilder.length() > 0) {
            pathBuilder.setLength(pathBuilder.length() - 1);
        }

        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=600x300&" +
                "path=color:0xfc4c02ff|weight:4|" + pathBuilder.toString() +
                "&key=" + GOOGLE_MAPS_API_KEY;

        // 2. Fetch the image bytes
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, byte[].class);

            // FOR LOCAL TESTING WITHOUT AN API KEY: Return a dummy 1x1 pixel byte array
            // so we don't crash when Google rejects the request for an invalid key.
            // return new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        } catch (Exception e) {
            System.err.println("Failed to fetch map image: " + e.getMessage());
            return new byte[0];
        }
    }
}