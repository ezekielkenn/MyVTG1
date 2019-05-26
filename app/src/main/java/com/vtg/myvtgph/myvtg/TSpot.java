package com.vtg.myvtgph.myvtg;

import com.google.firebase.firestore.GeoPoint;

public class TSpot {
    String id,place_name,place_desc,place_img;
    int place_rate;
    GeoPoint placeLoc;

//    public TSpot(String id, String place_name, String place_history, String place_image, int i, GeoPoint place_latlng) {
//    }

    public GeoPoint getPlaceLoc() {
        return placeLoc;
    }

    public void setPlaceLoc(GeoPoint placeLoc) {
        this.placeLoc = placeLoc;
    }

    public TSpot(String id, String place_name, String place_desc, String place_img, int place_rate, GeoPoint placeLoc) {

        this.id = id;
        this.place_name = place_name;
        this.place_desc = place_desc;
        this.place_img = place_img;
//        this.place_rate = place_rate;
        this.placeLoc=placeLoc;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_desc() {
        return place_desc;
    }

    public void setPlace_desc(String place_desc) {
        this.place_desc = place_desc;
    }

    public String getPlace_img() {
        return place_img;
    }

    public void setPlace_img(String place_img) {
        this.place_img = place_img;
    }
//
//    public int getPlace_rate() {
//        return place_rate;
//    }
//
//    public void setPlace_rate(int place_rate) {
//        this.place_rate = place_rate;
//    }
}
