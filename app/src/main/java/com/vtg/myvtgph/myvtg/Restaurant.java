package com.vtg.myvtgph.myvtg;

import com.google.firebase.firestore.GeoPoint;

public class Restaurant {
    String restaurantName,restaurantAddress,restaurantIcon;
    String open;
    float rating;
    GeoPoint location;

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantIcon() {
        return restaurantIcon;
    }

    public void setRestaurantIcon(String restaurantIcon) {
        this.restaurantIcon = restaurantIcon;
    }

    public String isOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Restaurant(String restaurantName, String restaurantAddress, String restaurantIcon, String open, float rating,GeoPoint location) {

        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantIcon = restaurantIcon;
        this.open = open;
        this.rating = rating;
        this.location =location;
    }
}
