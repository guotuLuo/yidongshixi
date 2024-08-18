package com.example.entity;

public class Device {
    public String id;
    public double latitude;
    public double longitude;
    public String geocode;

    public Device(){

    }
    public Device(String deviceId, double latitude, double longitude, String geocode) {
        this.id = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geocode = geocode;
    }
    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getGeocode() {
        return geocode;
    }

    @Override
    public String toString() {
        return "Device{id='" + id + "', longitude=" + longitude + ", latitude=" + latitude + ", geocode='" + geocode + "'}";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setGeocode(String geocode) {
        this.geocode = geocode;
    }
}
