package com.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "employment")
public class Employment {

    @Id
    @Column(name = "client_id")
    private Integer clientId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "client_id")
    @JsonBackReference("client-employment")
    private Client client;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    @Column(name = "salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;

    public Employment() {}

    public Employment(String businessName, String positionName, BigDecimal salary) {
        this.businessName = businessName;
        this.positionName = positionName;
        this.salary = salary;
    }

    public Integer getClientId() { return clientId; }
    public Client getClient() { return client; }
    public String getBusinessName() { return businessName; }
    public String getPositionName() { return positionName; }
    public BigDecimal getSalary() { return salary; }

    public void setClient(Client client) { this.client = client; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
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