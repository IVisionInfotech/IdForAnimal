package com.idforanimal.model;

import java.io.Serializable;

public class Animal implements Serializable {

    private String vaccinationData, ownerValidDate, animalId, customerId, empId, ownerId, oldOwnerId, taggingDate, rfidTagNo, visualTagNo,
            animalTypeId, cattleTypeId, breedTypeId, colorId, tailId, hornId, animalName, cattleName,
            breedName, colorName, tailName, hornName, visualSign, dob, validDate, age, girth, length, milperday,
            milktype, lactation, pregStatus, image, formDocument, remark, deleteStatus, area, catchedDate,
            vaccinationName, treatmentName, ownerName, ownerContact, totalCatching, totalTreatment,
            totalVaccination, otherVaccination, otherTreatment, vaccinationDate, type, catchCount, firstName,
            NEWOWNERFNAME, dateTimeAdded, newCustomerId;
    private String name;
    private String address;
    private String tenementNo;
    private String zoneName;
    private String wardName;
    private String contact;
    private String emailId;
    private String placeArea;
    private String remarks;
    private String catchingLocation;
    private String deathDate;
    private String disposedDate;
    private String treatmentData;
    private String deathCertificateNo;
    private String disposedReceipt;
    private String transferTaggingDate;
    private String deathStatus;
    private String reTaggingDate;
    private String operationDate;

    public String getAuditAnimalId() {
        return auditAnimalId;
    }

    public void setAuditAnimalId(String auditAnimalId) {
        this.auditAnimalId = auditAnimalId;
    }

    public void setNewCustomerId(String newCustomerId) {
        this.newCustomerId = newCustomerId;
    }

    private String auditAnimalId;

    public String getVaccinationName() {
        return vaccinationName;
    }

    public void setVaccinationName(String vaccinationName) {
        this.vaccinationName = vaccinationName;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getOwnerValidDate() {
        return ownerValidDate;
    }

    public void setOwnerValidDate(String ownerValidDate) {
        this.ownerValidDate = ownerValidDate;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public String getNewCustomerId() {
        return newCustomerId;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getOtherVaccination() {
        return otherVaccination;
    }

    public void setOtherVaccination(String otherVaccination) {
        this.otherVaccination = otherVaccination;
    }

    public String getOldOwnerId() {
        return oldOwnerId;
    }

    public void setOldOwnerId(String oldOwnerId) {
        this.oldOwnerId = oldOwnerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCatchedDate() {
        return catchedDate;
    }

    public void setCatchedDate(String catchedDate) {
        this.catchedDate = catchedDate;
    }

    public String getCatchCount() {
        return catchCount;
    }

    public void setCatchCount(String catchCount) {
        this.catchCount = catchCount;
    }


    public String getOLDOWNERNAME() {
        return NEWOWNERFNAME;
    }

    public void setOLDOWNERNAME(String NEWOWNERFNAME) {
        this.NEWOWNERFNAME = NEWOWNERFNAME;
    }

    public String getNEWOWNERFNAME() {
        return firstName;
    }

    public void setNEWOWNERFNAME(String firstName) {
        this.firstName = firstName;
    }


    public String getDateTimeAdded() {
        return dateTimeAdded;
    }

    public void setDateTimeAdded(String dateTimeAdded) {
        this.dateTimeAdded = dateTimeAdded;
    }


    public String getReTaggingDate() {
        return reTaggingDate;
    }

    public void setReTaggingDate(String reTaggingDate) {
        this.reTaggingDate = reTaggingDate;
    }


    public String getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(String operationDate) {
        this.operationDate = operationDate;
    }

    public String getOtherTreatment() {
        return otherTreatment;
    }

    public void setOtherTreatment(String otherTreatment) {
        this.otherTreatment = otherTreatment;
    }


    public String getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(String vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
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

    public String getTaggingDate() {
        return taggingDate;
    }

    public void setTaggingDate(String taggingDate) {
        this.taggingDate = taggingDate;
    }

    public String getRfidTagNo() {
        return rfidTagNo;
    }

    public void setRfidTagNo(String rfidTagNo) {
        this.rfidTagNo = rfidTagNo;
    }

    public String getVisualTagNo() {
        return visualTagNo;
    }

    public void setVisualTagNo(String visualTagNo) {
        this.visualTagNo = visualTagNo;
    }

    public String getAnimalTypeId() {
        return animalTypeId;
    }

    public void setAnimalTypeId(String animalTypeId) {
        this.animalTypeId = animalTypeId;
    }

    public String getCattleTypeId() {
        return cattleTypeId;
    }

    public void setCattleTypeId(String cattleTypeId) {
        this.cattleTypeId = cattleTypeId;
    }

    public String getBreedTypeId() {
        return breedTypeId;
    }

    public void setBreedTypeId(String breedTypeId) {
        this.breedTypeId = breedTypeId;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getTailId() {
        return tailId;
    }

    public void setTailId(String tailId) {
        this.tailId = tailId;
    }

    public String getHornId() {
        return hornId;
    }

    public void setHornId(String hornId) {
        this.hornId = hornId;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getCattleName() {
        return cattleName;
    }

    public void setCattleName(String cattleName) {
        this.cattleName = cattleName;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getTailName() {
        return tailName;
    }

    public void setTailName(String tailName) {
        this.tailName = tailName;
    }

    public String getHornName() {
        return hornName;
    }

    public String getVaccinationData() {
        return vaccinationData;
    }

    public void setVaccinationData(String vaccinationData) {
        this.vaccinationData = vaccinationData;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public String getTotalCatching() {
        return totalCatching;
    }

    public void setTotalCatching(String totalCatching) {
        this.totalCatching = totalCatching;
    }

    public String getTotalTreatment() {
        return totalTreatment;
    }

    public void setTotalTreatment(String totalTreatment) {
        this.totalTreatment = totalTreatment;
    }

    public String getTotalVaccination() {
        return totalVaccination;
    }

    public void setTotalVaccination(String totalVaccination) {
        this.totalVaccination = totalVaccination;
    }

    public String getTreatmentData() {
        return treatmentData;
    }

    public void setTreatmentData(String treatmentData) {
        this.treatmentData = treatmentData;
    }

    public void setHornName(String hornName) {
        this.hornName = hornName;
    }

    public String getVisualSign() {
        return visualSign;
    }

    public void setVisualSign(String visualSign) {
        this.visualSign = visualSign;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGirth() {
        return girth;
    }

    public void setGirth(String girth) {
        this.girth = girth;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMilperday() {
        return milperday;
    }

    public void setMilperday(String milperday) {
        this.milperday = milperday;
    }

    public String getMilktype() {
        return milktype;
    }

    public void setMilktype(String milktype) {
        this.milktype = milktype;
    }

    public String getLactation() {
        return lactation;
    }

    public void setLactation(String lactation) {
        this.lactation = lactation;
    }

    public String getPregStatus() {
        return pregStatus;
    }

    public void setPregStatus(String pregStatus) {
        this.pregStatus = pregStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFormDocument() {
        return formDocument;
    }

    public void setFormDocument(String formDocument) {
        this.formDocument = formDocument;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTenementNo() {
        return tenementNo;
    }

    public void setTenementNo(String tenementNo) {
        this.tenementNo = tenementNo;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPlaceArea() {
        return placeArea;
    }

    public void setPlaceArea(String placeArea) {
        this.placeArea = placeArea;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCatchingLocation() {
        return catchingLocation;
    }

    public void setCatchingLocation(String catchingLocation) {
        this.catchingLocation = catchingLocation;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getDisposedDate() {
        return disposedDate;
    }

    public void setDisposedDate(String disposedDate) {
        this.disposedDate = disposedDate;
    }

    public String getDeathCertificateNo() {
        return deathCertificateNo;
    }

    public void setDeathCertificateNo(String deathCertificateNo) {
        this.deathCertificateNo = deathCertificateNo;
    }

    public String getDisposedReceipt() {
        return disposedReceipt;
    }

    public void setDisposedReceipt(String disposedReceipt) {
        this.disposedReceipt = disposedReceipt;
    }

    public String getTransferTaggingDate() {
        return transferTaggingDate;
    }

    public void setTransferTaggingDate(String transferTaggingDate) {
        this.transferTaggingDate = transferTaggingDate;
    }

    public String getDeathStatus() {
        return deathStatus;
    }

    public void setDeathStatus(String deathStatus) {
        this.deathStatus = deathStatus;
    }
}
