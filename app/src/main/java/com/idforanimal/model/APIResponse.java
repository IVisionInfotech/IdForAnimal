package com.idforanimal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class APIResponse {

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("result")
    @Expose
    private Animal animal;

    @SerializedName("stateArray")
    @Expose
    private ArrayList<CommonModel> stateList;

    @SerializedName("cityArray")
    @Expose
    private ArrayList<CommonModel> cityList;

    @SerializedName("zoneArray")
    @Expose
    private ArrayList<CommonModel> zoneList;

    @SerializedName("wardArray")
    @Expose
    private ArrayList<CommonModel> wardList;

    @SerializedName("animalTypeArray")
    @Expose
    private ArrayList<CommonModel> animalTypeList;

    @SerializedName("subTypeArray")
    @Expose
    private ArrayList<CommonModel> subTypeList;

    @SerializedName("breedTypeArray")
    @Expose
    private ArrayList<CommonModel> breedTypeList;

    @SerializedName("colorArray")
    @Expose
    private ArrayList<CommonModel> colorList;

    @SerializedName("tailArray")
    @Expose
    private ArrayList<CommonModel> tailList;

    @SerializedName("hornArray")
    @Expose
    private ArrayList<CommonModel> hornList;

    @SerializedName("animalOwnerArray")
    @Expose
    private ArrayList<AnimalOwner> animalOwnerList;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @SerializedName("ownerId")
    @Expose
    private String ownerId;

    public ArrayList<CommonModel> getOperationList() {
        return operationList;
    }

    public void setOperationList(ArrayList<CommonModel> operationList) {
        this.operationList = operationList;
    }

    @SerializedName("operationList")
    @Expose
    private ArrayList<CommonModel> operationList;

    public ArrayList<Release> getReleaseArray() {
        return releaseArray;
    }

    public void setReleaseArray(ArrayList<Release> releaseArray) {
        this.releaseArray = releaseArray;
    }

    @SerializedName("totalAnimalCount")
    @Expose
    private String totalAnimalCount;

    @SerializedName("totalAuditCount")
    @Expose
    private String totalAuditCount;

    @SerializedName("totalPendingCount")
    @Expose
    private String totalPendingCount;

    public String getTotalExtraCount() {
        return totalExtraCount;
    }

    public String getTotalPendingCount() {
        return totalPendingCount;
    }

    public String getTotalAuditCount() {
        return totalAuditCount;
    }

    public String getTotalAnimalCount() {
        return totalAnimalCount;
    }

    @SerializedName("totalExtraCount")
    @Expose
    private String totalExtraCount;

    @SerializedName("releaseArray")
    @Expose
    private ArrayList<Release> releaseArray;
    @SerializedName("animalOwnerDetails")
    @Expose
    private AnimalOwner animalOwnerDetails;

    public Catching getAnimalCatchingDetails() {
        return animalCatchingDetails;
    }

    public void setAnimalCatchingDetails(Catching animalCatchingDetails) {
        this.animalCatchingDetails = animalCatchingDetails;
    }

    @SerializedName("animalCatchingDetails")
    @Expose
    private Catching animalCatchingDetails;

    @SerializedName("animalArray")
    @Expose
    private ArrayList<Animal> animalList;

    @SerializedName("animalDetails")
    @Expose
    private Animal animalDetails;
    @SerializedName("countDetails")
    @Expose
    private CommonModel countDetails;

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public void setStateList(ArrayList<CommonModel> stateList) {
        this.stateList = stateList;
    }

    public void setCityList(ArrayList<CommonModel> cityList) {
        this.cityList = cityList;
    }

    public void setZoneList(ArrayList<CommonModel> zoneList) {
        this.zoneList = zoneList;
    }

    public void setWardList(ArrayList<CommonModel> wardList) {
        this.wardList = wardList;
    }

    public void setAnimalTypeList(ArrayList<CommonModel> animalTypeList) {
        this.animalTypeList = animalTypeList;
    }

    public void setSubTypeList(ArrayList<CommonModel> subTypeList) {
        this.subTypeList = subTypeList;
    }

    public void setBreedTypeList(ArrayList<CommonModel> breedTypeList) {
        this.breedTypeList = breedTypeList;
    }

    public void setColorList(ArrayList<CommonModel> colorList) {
        this.colorList = colorList;
    }

    public void setTailList(ArrayList<CommonModel> tailList) {
        this.tailList = tailList;
    }

    public void setHornList(ArrayList<CommonModel> hornList) {
        this.hornList = hornList;
    }

    public void setAnimalOwnerList(ArrayList<AnimalOwner> animalOwnerList) {
        this.animalOwnerList = animalOwnerList;
    }

    public void setAnimalOwnerDetails(AnimalOwner animalOwnerDetails) {
        this.animalOwnerDetails = animalOwnerDetails;
    }

    public void setAnimalList(ArrayList<Animal> animalList) {
        this.animalList = animalList;
    }

    public void setAnimalDetails(Animal animalDetails) {
        this.animalDetails = animalDetails;
    }

    public void setCountDetails(CommonModel countDetails) {
        this.countDetails = countDetails;
    }

    @SerializedName("pondArray")
    @Expose
    private List<Pond> pondArray;

    public List<Animal> getAnimalHistory() {
        return animalHistory;
    }

    public void setAnimalHistory(List<Animal> animalHistory) {
        this.animalHistory = animalHistory;
    }

    @SerializedName("animalHistoryList")
    @Expose
    private List<Animal> animalHistory;

    public List<Catching> getCatchingList() {
        return catchingList;
    }

    public void setCatchingList(List<Catching> catchingList) {
        this.catchingList = catchingList;
    }

    public List<AuditViewModel> getAuditList() {
        return auditList;
    }

    public void setAuditList(List<AuditViewModel> auditList) {
        this.auditList = auditList;
    }

    @SerializedName("catchingArray")
    @Expose
    private List<Catching> catchingList;
    @SerializedName("auditList")
    @Expose
    private List<AuditViewModel> auditList;

    public List<Pond> getPondArray() {
        return pondArray;
    }

    public void setPondArray(List<Pond> pondArray) {
        this.pondArray = pondArray;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Animal getAnimal() {
        return animal;
    }

    public ArrayList<CommonModel> getStateList() {
        return stateList;
    }

    public ArrayList<CommonModel> getCityList() {
        return cityList;
    }

    public ArrayList<CommonModel> getZoneList() {
        return zoneList;
    }

    public ArrayList<CommonModel> getWardList() {
        return wardList;
    }

    public ArrayList<CommonModel> getAnimalTypeList() {
        return animalTypeList;
    }

    public ArrayList<CommonModel> getSubTypeList() {
        return subTypeList;
    }

    public ArrayList<CommonModel> getBreedTypeList() {
        return breedTypeList;
    }

    public ArrayList<CommonModel> getColorList() {
        return colorList;
    }

    public ArrayList<CommonModel> getTailList() {
        return tailList;
    }

    public ArrayList<CommonModel> getHornList() {
        return hornList;
    }

    public ArrayList<AnimalOwner> getAnimalOwnerList() {
        return animalOwnerList;
    }

    public AnimalOwner getAnimalOwnerDetails() {
        return animalOwnerDetails;
    }

    public ArrayList<Animal> getAnimalList() {
        return animalList;
    }

    public Animal getAnimalDetails() {
        return animalDetails;
    }

    public CommonModel getCountDetails() {
        return countDetails;
    }
}