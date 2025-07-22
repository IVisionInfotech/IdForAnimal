
package com.idforanimal.model;

import java.io.Serializable;

public class AnimalOwner implements Serializable {

    private String ownerId;
    private String registrationDate;
    private String registrationNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String contact;
    private String emailId;
    private String zoneName;
    private String wardName;
    private String address;
    private String remarks;
    private String liveAnimalCount;
    private String deathAnimalCount;

    public String getPondId() {
        return pondId;
    }

    public void setPondId(String pondId) {
        this.pondId = pondId;
    }

    public String getPondStatus() {
        return pondStatus;
    }

    public void setPondStatus(String pondStatus) {
        this.pondStatus = pondStatus;
    }

    private String pondId;
    private String pondStatus;

    private String ngoId;

    public String getNgoId() {
        return ngoId;
    }

    public void setNgoId(String ngoId) {
        this.ngoId = ngoId;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    private String ngoName;
    private String contactPersonName;
    private String aadharNo,validDate, aadharFront, aadharBack, electionCardNo, electionFront, electionBack, dlNo, dlFront, dlBack,
            gender, houseNo, street, landmark, area, countryName, stateId, stateName, districtName, cityId, cityName,
            pincode, tenementNo, zoneId, wardId, contact1, placeArea, placeOwned, shedAvailable, storageAvailable,
            drinkingWater, disposalFacility, photoNo, formDocument;

    public String getOwnerId() {
        return ownerId;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getContact() {
        return contact;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getWardName() {
        return wardName;
    }

    public String getAddress() {
        return address;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getLiveAnimalCount() {
        return liveAnimalCount;
    }

    public void setLiveAnimalCount(String liveAnimalCount) {
        this.liveAnimalCount = liveAnimalCount;
    }

    public String getDeathAnimalCount() {
        return deathAnimalCount;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public String getAadharFront() {
        return aadharFront;
    }

    public String getAadharBack() {
        return aadharBack;
    }

    public String getElectionCardNo() {
        return electionCardNo;
    }

    public String getElectionFront() {
        return electionFront;
    }

    public String getElectionBack() {
        return electionBack;
    }

    public String getDlNo() {
        return dlNo;
    }

    public String getDlFront() {
        return dlFront;
    }

    public String getDlBack() {
        return dlBack;
    }

    public String getGender() {
        return gender;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getStreet() {
        return street;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getArea() {
        return area;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getStateId() {
        return stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPincode() {
        return pincode;
    }

    public String getTenementNo() {
        return tenementNo;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getWardId() {
        return wardId;
    }

    public String getContact1() {
        return contact1;
    }

    public String getPlaceArea() {
        return placeArea;
    }

    public String getPlaceOwned() {
        return placeOwned;
    }

    public String getShedAvailable() {
        return shedAvailable;
    }

    public String getStorageAvailable() {
        return storageAvailable;
    }

    public String getDrinkingWater() {
        return drinkingWater;
    }

    public String getDisposalFacility() {
        return disposalFacility;
    }

    public String getPhotoNo() {
        return photoNo;
    }

    public String getFormDocument() {
        return formDocument;
    }
}
