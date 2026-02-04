package com.example.model;

public class Client {
    private Integer clientId; // nullable for "create" before DB assigns it
    private String firstName;
    private String lastName;
    private String employment;

    public Client() {}

    public Client(Integer clientId, String firstName, String lastName, String employment) {
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employment = employment;
    }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmployment() { return employment; }
    public void setEmployment(String employment) { this.employment = employment; }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employment='" + employment + '\'' +
                '}';
    }
}