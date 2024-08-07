package com.example.entity;

public class Device {
    public  String deviceId;
    public double latitude;
    public double longitude;
    public String geocode;

    public Device(String deviceId, double latitude, double longitude, String geocode) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geocode = geocode;
    }

    public double getLongitude() {
        return  this.longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public String getDeviceId(){
        return this.deviceId;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + deviceId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", geocode='" + geocode + '\'' +
                '}';
    }
}
