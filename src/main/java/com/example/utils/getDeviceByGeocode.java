package com.example.utils;

import com.example.entity.Device;

import java.util.List;

public class getDeviceByGeocode {
    public static Device getDeviceByGeocode(String geocode, List<Device> devicesInPolygon) {
        return devicesInPolygon.stream()
                .filter(device -> device.getGeocode().equals(geocode))
                .findFirst()
                .orElse(null);
    }
}
