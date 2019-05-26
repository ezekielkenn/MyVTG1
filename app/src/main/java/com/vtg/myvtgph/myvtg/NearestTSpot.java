package com.vtg.myvtgph.myvtg;

import com.google.firebase.firestore.GeoPoint;

public class NearestTSpot {
    String place_id,place_name,place_history,place_img;
    int rating;
    double distance;
    GeoPoint latlng;

    public NearestTSpot(String place_id, String place_name, String place_history, String place_img, int rating, double distance, GeoPoint latlng) {
        this.place_id = place_id;
        this.place_name = place_name;
        this.place_history = place_history;
        this.place_img = place_img;
        this.rating = rating;
        this.distance = distance;
        this.latlng = latlng;
    }

    public String getPlace_id() {

        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_history() {
        return place_history;
    }

    public void setPlace_history(String place_history) {
        this.place_history = place_history;
    }

    public String getPlace_img() {
        return place_img;
    }

    public void setPlace_img(String place_img) {
        this.place_img = place_img;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GeoPoint getLatlng() {
        return latlng;
    }

    public void setLatlng(GeoPoint latlng) {
        this.latlng = latlng;
    }
}
