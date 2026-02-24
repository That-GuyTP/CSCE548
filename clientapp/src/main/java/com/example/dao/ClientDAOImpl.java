package com.example.dao;

import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;
import com.example.repo.ClientRepository;
import com.example.repo.EmploymentRepository;
import com.example.repo.PersonRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ClientDAOImpl implements ClientDAO {

    private final ClientRepository clientRepo;
    private final PersonRepository personRepo;
    private final EmploymentRepository employmentRepo;

    public ClientDAOImpl(ClientRepository clientRepo, PersonRepository personRepo, EmploymentRepository employmentRepo) {
        this.clientRepo = clientRepo;
        this.personRepo = personRepo;
        this.employmentRepo = employmentRepo;
    }

    @Override
    public Client createClient(Client client) {
        return clientRepo.save(client);
    }

    @Override
    public Optional<Client> getClient(Integer clientId) {
        return clientRepo.findById(clientId);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }

    @Override
    public Client updateClient(Integer clientId, String firstName, String lastName, String employment) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmployment(employment);
        return clientRepo.save(c);
    }

    @Override
    public void deleteClient(Integer clientId) {
        clientRepo.deleteById(clientId);
    }

    @Override
    public Person upsertPerson(Integer clientId, Person person) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        c.setPerson(person);
        clientRepo.save(c); // cascades Person
        return personRepo.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Person not saved for client: " + clientId));
    }

    @Override
    public Optional<Person> getPerson(Integer clientId) {
        return personRepo.findById(clientId);
    }

    @Override
    public void deletePerson(Integer clientId) {
        personRepo.deleteById(clientId);
    }

    @Override
    public Employment upsertEmployment(Integer clientId, Employment employment) {
        Client c = clientRepo.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        c.setEmploymentDetails(employment);
        clientRepo.save(c); // cascades Employment
        return employmentRepo.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Employment not saved for client: " + clientId));
    }

    @Override
    public Optional<Employment> getEmployment(Integer clientId) {
        return employmentRepo.findById(clientId);
    }

    @Override
    public void deleteEmployment(Integer clientId) {
        employmentRepo.deleteById(clientId);
    }
}