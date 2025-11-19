package com.pds.logisticsservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/*
    The client uses this to communicate with location-service for distance calculation between the two lat/lng pairs
 */

//TODO: match base URL to location-service port
//TODO: add error handling for failures/timeouts
//TODO: find out how to optimize async calls

@Service
public class DistanceServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    //=============================================================================================
    // Calls location-service to compute the distance
    //===================================================================================
    public double getDistance (double lat1, double lng1, double lat2, double lng2) {

        //TODO: match the URL to location-service port
        String url = "";

        return restTemplate.getForObject(url, Double.class);
    }
}
