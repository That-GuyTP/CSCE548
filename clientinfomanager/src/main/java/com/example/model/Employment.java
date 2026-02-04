package com.example.model;

import java.math.BigDecimal;

public class Employment {
    private int clientId; // FK -> client.client_id (and PK here)
    private String businessName;
    private String positionName;
    private BigDecimal salary;

    public Employment() {}

    public Employment(int clientId, String businessName, String positionName, BigDecimal salary) {
        this.clientId = clientId;
        this.businessName = businessName;
        this.positionName = positionName;
        this.salary = salary;
    }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    @Override
    public String toString() {
        return "Employment{" +
                "clientId=" + clientId +
                ", businessName='" + businessName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", salary=" + salary +
                '}';
    }
}