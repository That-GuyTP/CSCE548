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
        if (!hasText(firstName) && !hasText(lastName) && !hasText(employment)) {
            throw new InvalidRequestException("At least one client field is required for update.");
        }

        // Ensure the client exists (business-layer responsibility)
        Client existing = dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found: " + clientId));

        String updatedFirstName = hasText(firstName) ? firstName.trim() : existing.getFirstName();
        String updatedLastName = hasText(lastName) ? lastName.trim() : existing.getLastName();
        String updatedEmployment = hasText(employment) ? employment.trim() : existing.getEmployment();

        return dao.updateClient(clientId, updatedFirstName, updatedLastName, updatedEmployment);
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

        // Ensure parent exists (service-level rule)
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found for Person: " + clientId));

        Optional<Person> existingPerson = dao.getPerson(clientId);
        if (existingPerson.isPresent()) {
            Person existing = existingPerson.get();
            if (!hasText(person.getFirstName()) &&
                    !hasText(person.getLastName()) &&
                    person.getDateOfBirth() == null &&
                    !hasText(person.getAddress()) &&
                    !hasText(person.getLegalSex())) {
                throw new InvalidRequestException("At least one person field is required for update.");
            }

            Person merged = new Person(
                    hasText(person.getFirstName()) ? person.getFirstName().trim() : existing.getFirstName(),
                    hasText(person.getLastName()) ? person.getLastName().trim() : existing.getLastName(),
                    person.getDateOfBirth() != null ? person.getDateOfBirth() : existing.getDateOfBirth(),
                    hasText(person.getAddress()) ? person.getAddress().trim() : existing.getAddress(),
                    hasText(person.getLegalSex()) ? person.getLegalSex().trim() : existing.getLegalSex()
            );
            return dao.upsertPerson(clientId, merged);
        }

        requireNonBlank(person.getFirstName(), "Person firstName is required.");
        requireNonBlank(person.getLastName(), "Person lastName is required.");
        if (person.getDateOfBirth() == null) throw new InvalidRequestException("Person dateOfBirth is required.");
        requireNonBlank(person.getAddress(), "Person address is required.");
        requireNonBlank(person.getLegalSex(), "Person legalSex is required.");

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

        // Ensure parent exists
        dao.getClient(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found for Employment: " + clientId));

        Optional<Employment> existingEmployment = dao.getEmployment(clientId);
        if (existingEmployment.isPresent()) {
            Employment existing = existingEmployment.get();
            if (!hasText(employment.getBusinessName()) &&
                    !hasText(employment.getPositionName()) &&
                    employment.getSalary() == null) {
                throw new InvalidRequestException("At least one employment field is required for update.");
            }

            Employment merged = new Employment(
                    hasText(employment.getBusinessName()) ? employment.getBusinessName().trim() : existing.getBusinessName(),
                    hasText(employment.getPositionName()) ? employment.getPositionName().trim() : existing.getPositionName(),
                    employment.getSalary() != null ? employment.getSalary() : existing.getSalary()
            );
            return dao.upsertEmployment(clientId, merged);
        }

        requireNonBlank(employment.getBusinessName(), "Employment businessName is required.");
        requireNonBlank(employment.getPositionName(), "Employment positionName is required.");
        if (employment.getSalary() == null) throw new InvalidRequestException("Employment salary is required.");

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
        if (!hasText(val)) throw new InvalidRequestException(msg);
    }

    private boolean hasText(String val) {
        return val != null && !val.trim().isEmpty();
    }
}
