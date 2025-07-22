package com.idforanimal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Release implements Serializable {


    private String releaseId;

    private String customerId;

    private String empId;

    private String ownerId;

    private String releaseDate;

    private String firNo;

    private String chargeSheetNo;

    private String affidavitNo;

    private String paymentReceiptNo;

    private String remark;

    private String animalIds;

    private String deleteStatus;

    private String dateTimeAdded;
    @SerializedName("animalArray")
    @Expose
    private List<Animal> animalList;
    private String area;

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    private String landmark;

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFirNo() {
        return firNo;
    }

    public void setFirNo(String firNo) {
        this.firNo = firNo;
    }

    public String getChargeSheetNo() {
        return chargeSheetNo;
    }

    public void setChargeSheetNo(String chargeSheetNo) {
        this.chargeSheetNo = chargeSheetNo;
    }

    public String getAffidavitNo() {
        return affidavitNo;
    }

    public void setAffidavitNo(String affidavitNo) {
        this.affidavitNo = affidavitNo;
    }

    public String getPaymentReceiptNo() {
        return paymentReceiptNo;
    }

    public void setPaymentReceiptNo(String paymentReceiptNo) {
        this.paymentReceiptNo = paymentReceiptNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAnimalIds() {
        return animalIds;
    }

    public void setAnimalIds(String animalIds) {
        this.animalIds = animalIds;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getDateTimeAdded() {
        return dateTimeAdded;
    }

    public void setDateTimeAdded(String dateTimeAdded) {
        this.dateTimeAdded = dateTimeAdded;
    }

    public List<Animal> getAnimalList() {
        return animalList;
    }

    public void setAnimalList(List<Animal> animalList) {
        this.animalList = animalList;
    }
}
