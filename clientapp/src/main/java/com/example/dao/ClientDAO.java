package com.example.dao;

import com.example.model.Client;
import com.example.model.Person;
import com.example.model.Employment;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {

    Client createClient(Client client);
    Optional<Client> getClient(Integer clientId);
    List<Client> getAllClients();
    Client updateClient(Integer clientId, String firstName, String lastName, String employment);
    void deleteClient(Integer clientId);

    Person upsertPerson(Integer clientId, Person person);
    Optional<Person> getPerson(Integer clientId);
    void deletePerson(Integer clientId);

    Employment upsertEmployment(Integer clientId, Employment employment);
    Optional<Employment> getEmployment(Integer clientId);
    void deleteEmployment(Integer clientId);
}