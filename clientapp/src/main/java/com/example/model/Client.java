package com.example.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // This matches: client.employment VARCHAR(100) NOT NULL
    // It's an "employment type/status" in your seed data (W2/1099/Unemployed).
    @Column(name = "employment", nullable = false, length = 100)
    private String employment;

    // Optional object relationships to the 1:1 tables.
    // These do NOT replace the "employment" VARCHAR column above.
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("client-person")
    private Person person;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("client-employment")
    private Employment employmentDetails;

    public Client() {}

    public Client(String firstName, String lastName, String employment) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.employment = employment;
    }

    public Integer getClientId() { return clientId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmployment() { return employment; }
    public Person getPerson() { return person; }
    public Employment getEmploymentDetails() { return employmentDetails; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmployment(String employment) { this.employment = employment; }

    public void setPerson(Person person) {
        this.person = person;
        if (person != null) person.setClient(this);
    }

    public void setEmploymentDetails(Employment employmentDetails) {
        this.employmentDetails = employmentDetails;
        if (employmentDetails != null) employmentDetails.setClient(this);
    }

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