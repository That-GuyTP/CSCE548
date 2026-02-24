package com.example.service;

import com.example.dao.ClientDAO;
import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;
import com.example.exception.InvalidRequestException;
import com.example.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientDAO dao;

    public ClientServiceImpl(ClientDAO dao) {
        this.dao = dao;
    }

    // -----------------------
    // Client CRUD
    // -----------------------

    @Override
    @Transactional
    public Client createClient(Client client) {
        if (client == null) throw new InvalidRequestException("Client cannot be null.");
        requireNonBlank(client.getFirstName(), "Client firstName is required.");
        requireNonBlank(client.getLastName(), "Client lastName is required.");
        requireNonBlank(client.getEmployment(), "Client employment is required.");

        return dao.createClient(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClient(Integer clientId) {
        requireId(clientId);
        return dao.getClient(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return dao.getAllClients();
    }

    @Override
    @Transactional
    public Client updateClient(Integer clientId, String firstName, String lastName, String employment) {
        requireId(clientId);
        requireNonBlank(firstName, "firstName is required.");
        requireNonBlank(lastName, "lastName is required.");
        requireNonBlank(employment, "employment is required.");

        // Ensure the client exists (business-layer responsibility)
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found: " + clientId));

        return dao.updateClient(clientId, firstName, lastName, employment);
    }

    @Override
    @Transactional
    public void deleteClient(Integer clientId) {
        requireId(clientId);
        // Optional existence check (recommended so UI gets a clean error)
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found: " + clientId));
        dao.deleteClient(clientId);
    }

    // -----------------------
    // Person CRUD
    // -----------------------

    @Override
    @Transactional
    public Person upsertPerson(Integer clientId, Person person) {
        requireId(clientId);
        if (person == null) throw new InvalidRequestException("Person cannot be null.");
        requireNonBlank(person.getFirstName(), "Person firstName is required.");
        requireNonBlank(person.getLastName(), "Person lastName is required.");
        if (person.getDateOfBirth() == null) throw new InvalidRequestException("Person dateOfBirth is required.");
        requireNonBlank(person.getAddress(), "Person address is required.");
        requireNonBlank(person.getLegalSex(), "Person legalSex is required.");

        // Ensure parent exists (service-level rule)
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found for Person: " + clientId));

        return dao.upsertPerson(clientId, person);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> getPerson(Integer clientId) {
        requireId(clientId);
        return dao.getPerson(clientId);
    }

    @Override
    @Transactional
    public void deletePerson(Integer clientId) {
        requireId(clientId);
        // Optional existence check for better UX
        dao.getPerson(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Person not found for client: " + clientId));
        dao.deletePerson(clientId);
    }

    // -----------------------
    // Employment CRUD
    // -----------------------

    @Override
    @Transactional
    public Employment upsertEmployment(Integer clientId, Employment employment) {
        requireId(clientId);
        if (employment == null) throw new InvalidRequestException("Employment cannot be null.");
        requireNonBlank(employment.getBusinessName(), "Employment businessName is required.");
        requireNonBlank(employment.getPositionName(), "Employment positionName is required.");
        if (employment.getSalary() == null) throw new InvalidRequestException("Employment salary is required.");

        // Ensure parent exists
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found for Employment: " + clientId));

        return dao.upsertEmployment(clientId, employment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employment> getEmployment(Integer clientId) {
        requireId(clientId);
        return dao.getEmployment(clientId);
    }

    @Override
    @Transactional
    public void deleteEmployment(Integer clientId) {
        requireId(clientId);
        dao.getEmployment(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Employment not found for client: " + clientId));
        dao.deleteEmployment(clientId);
    }

    // -----------------------
    // Helpers
    // -----------------------

    private void requireId(Integer id) {
        if (id == null || id <= 0) throw new InvalidRequestException("Valid ID is required.");
    }

    private void requireNonBlank(String val, String msg) {
        if (val == null || val.trim().isEmpty()) throw new InvalidRequestException(msg);
    }
}