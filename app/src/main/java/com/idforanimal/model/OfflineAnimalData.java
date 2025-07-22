package com.idforanimal.model;

public class OfflineAnimalData {

    public String getMicroChipNo() {
        return microChipNo;
    }

    public void setMicroChipNo(String microChipNo) {
        this.microChipNo = microChipNo;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String microChipNo, latitude,longitude;
}
