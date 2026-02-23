package com.example.project;

public class AllergyModel {

    String userId;
    String allergyName;
    String severity;
    String remarks;

    // Constructor
    public AllergyModel(String userId, String allergyName, String severity, String remarks) {
        this.userId = userId;
        this.allergyName = allergyName;
        this.severity = severity;
        this.remarks = remarks;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public String getSeverity() {
        return severity;
    }

    public String getRemarks() {
        return remarks;
    }
}
