package com.example.model;

import java.time.LocalDate;

public class Person {
    private int clientId; // FK -> client.client_id (and PK here)
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private String legalSex;

    public Person() {}

    public Person(int clientId, String firstName, String lastName, LocalDate dateOfBirth, String address, String legalSex) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.legalSex = legalSex;
    }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLegalSex() { return legalSex; }
    public void setLegalSex(String legalSex) { this.legalSex = legalSex; }

    @Override
    public String toString() {
        return "Person{" +
                "clientId=" + clientId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", legalSex='" + legalSex + '\'' +
                '}';
    }
}