package com.pds.location.service;

import com.pds.location.model.Warehouse;
import com.pds.location.repository.WarehouseRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private GoogleMapsService googleMapsService;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public Warehouse findNearestWarehouse(double lat, double lon) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) throw new RuntimeException("No warehouses found");

        return warehouses.stream()
                .min(Comparator.comparingDouble(w -> distance(lat, lon, w.getLatitude(), w.getLongitude())))
                .orElseThrow();
    }

    public Warehouse findNearestWarehouseByAddress(String address) throws JSONException {
        double[] coords = googleMapsService.geocodeAddress(address);
        return findNearestWarehouse(coords[0], coords[1]);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
