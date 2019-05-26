package com.vtg.myvtgph.myvtg;

import com.google.firebase.Timestamp;

public class Visits {
    String placeName;
    String placeHistory;
    String placeImgUrl;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    String placeId;
    Timestamp dateVisited;
    int myRate;

    public Visits() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceHistory() {
        return placeHistory;
    }

    public void setPlaceHistory(String placeHistory) {
        this.placeHistory = placeHistory;
    }

    public String getPlaceImgUrl() {
        return placeImgUrl;
    }

    public void setPlaceImgUrl(String placeImgUrl) {
        this.placeImgUrl = placeImgUrl;
    }

    public Timestamp getDateVisited() {
        return dateVisited;
    }

    public void setDateVisited(Timestamp dateVisited) {
        this.dateVisited = dateVisited;
    }

    public int getMyRate() {
        return myRate;
    }

    public void setMyRate(int myRate) {
        this.myRate = myRate;
    }

    public Visits(String placeName, String placeHistory, String placeImgUrl, Timestamp dateVisited, int myRate,String placeId) {

        this.placeName = placeName;
        this.placeHistory = placeHistory;
        this.placeImgUrl = placeImgUrl;
        this.dateVisited = dateVisited;
        this.myRate = myRate;
        this.placeId = placeId;
    }
}
