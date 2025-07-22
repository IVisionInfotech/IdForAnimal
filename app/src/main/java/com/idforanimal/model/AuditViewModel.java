package com.idforanimal.model;

import androidx.lifecycle.ViewModel;

public class AuditViewModel extends ViewModel {
    private String auditId;
    private String startDate;
    private String endDate;
    private String auditbyInstitution;
    private String auditbyPerson;
    private String status; // 0: Open, 1: Close
    private String customerId;
    private String empId;
    private String updatedAt;
    private String deleteStatus;
    private String dateTimeAdded;

    public boolean isVisibleDeleted() {
        return isDeleted;
    }

    private boolean isDeleted;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getAuditByInstitution() {
        return auditbyInstitution;
    }

    public void setAuditByInstitution(String auditbyInstitution) {
        this.auditbyInstitution = auditbyInstitution;
    }

    public String getAuditByPerson() {
        return auditbyPerson;
    }

    public void setAuditByPerson(String auditbyPerson) {
        this.auditbyPerson = auditbyPerson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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
}

