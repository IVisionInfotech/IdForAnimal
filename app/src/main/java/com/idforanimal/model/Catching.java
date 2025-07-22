package com.idforanimal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Catching implements Serializable {

    private String catchingId;
    private String area;
    private String landmark;
    private String catchedDate;
    private String catchedTime;
    private String pondName;
    private String zoneName;
    private String wardName;
    private String remark;
    private String pondId;
    private String zoneId;
    private String wardId;

    public String getAnimalIds() {
        return animalIds;
    }

    public void setAnimalIds(String animalIds) {
        this.animalIds = animalIds;
    }

    private String animalIds;
    @SerializedName("animalArray")
    @Expose
    private ArrayList<Animal> animalList;

    public ArrayList<Animal> getAnimalList() {
        return animalList;
    }

    public void setAnimalList(ArrayList<Animal> animalList) {
        this.animalList = animalList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPondId() {
        return pondId;
    }

    public void setPondId(String pondId) {
        this.pondId = pondId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getWardId() { return wardId; }

    public void setWardId(String wardId) { this.wardId = wardId; }
    public String getCatchingId() {
        return catchingId;
    }

    public void setCatchingId(String catchingId) {
        this.catchingId = catchingId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCatchedDate() {
        return catchedDate;
    }

    public void setCatchedDate(String catchedDate) {
        this.catchedDate = catchedDate;
    }

    public String getCatchedTime() {
        return catchedTime;
    }

    public void setCatchedTime(String catchedTime) {
        this.catchedTime = catchedTime;
    }

    public String getPondName() {
        return pondName;
    }

    public void setPondName(String pondName) {
        this.pondName = pondName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }
}
