package com.vtg.myvtgph.myvtg;

public class TouristSpotList {
    String placeName,placeHistory,placeImgUrl,place_image;

    public TouristSpotList(String place_name, String place_history, String place_image) {
    }

    public TouristSpotList(String test, String test1) {
    }


    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    String placeId;

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

    public TouristSpotList(String place_id, String placeName, String placeHistory, String placeImgUrl) {
        this.placeName = placeName;
        this.placeHistory = placeName;
        this.placeImgUrl = placeImgUrl;
    }
}
