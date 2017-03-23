package com.example.gpstracker;

/**
 * Created by Miguemc on 20/02/2017.
 */

public class Punto {

    private double latitude;
    private double longitude;


    public Punto(double longitude, double latitude) {
        this.longitude= longitude;
        this.latitude= latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public String toString() {
        return ("longitud:" + longitude + ", latitud:"+ latitude);
    }
    public double distancia(Punto punto) {
        double dLat = Math.toRadians(latitude - punto.latitude);
        double dLon = Math.toRadians(longitude - punto.longitude);
        double lat1 = Math.toRadians(punto.latitude);
        double lat2 = Math.toRadians(latitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) *
                        Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return c * 6371000;
    }
}
