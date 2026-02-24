package com.example.service;

import com.example.model.Client;
import com.example.model.Person;
import com.example.model.Employment;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    // Client CRUD
    Client createClient(Client client);
    Optional<Client> getClient(Integer clientId);
    List<Client> getAllClients();
    Client updateClient(Integer clientId, String firstName, String lastName, String employment);
    void deleteClient(Integer clientId);

    // Person CRUD (by client_id)
    Person upsertPerson(Integer clientId, Person person);
    Optional<Person> getPerson(Integer clientId);
    void deletePerson(Integer clientId);

    // Employment CRUD (by client_id)
    Employment upsertEmployment(Integer clientId, Employment employment);
    Optional<Employment> getEmployment(Integer clientId);
    void deleteEmployment(Integer clientId);
}