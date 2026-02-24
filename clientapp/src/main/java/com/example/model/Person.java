package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @Column(name = "client_id")
    private Integer clientId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "client_id")
    @JsonBackReference("client-person")
    private Client client;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "legal_sex", nullable = false, length = 20)
    private String legalSex;

    public Person() {}

    public Person(String firstName, String lastName, LocalDate dateOfBirth, String address, String legalSex) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.legalSex = legalSex;
    }

    public Integer getClientId() { return clientId; }
    public Client getClient() { return client; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getAddress() { return address; }
    public String getLegalSex() { return legalSex; }

    public void setClient(Client client) { this.client = client; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setAddress(String address) { this.address = address; }
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