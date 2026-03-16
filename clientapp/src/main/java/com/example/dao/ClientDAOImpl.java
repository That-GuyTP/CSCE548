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

        // Keep person name fields synchronized with client names when a person record exists.
        personRepo.findById(clientId).ifPresent(person -> {
            person.setFirstName(firstName);
            person.setLastName(lastName);
            personRepo.save(person);
        });

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
        return personRepo.findById(clientId)
                .map(existing -> {
                    existing.setFirstName(person.getFirstName());
                    existing.setLastName(person.getLastName());
                    existing.setDateOfBirth(person.getDateOfBirth());
                    existing.setAddress(person.getAddress());
                    existing.setLegalSex(person.getLegalSex());
                    return personRepo.save(existing);
                })
                .orElseGet(() -> {
                    person.setClient(c);
                    return personRepo.save(person);
                });
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
        return employmentRepo.findById(clientId)
                .map(existing -> {
                    existing.setBusinessName(employment.getBusinessName());
                    existing.setPositionName(employment.getPositionName());
                    existing.setSalary(employment.getSalary());
                    return employmentRepo.save(existing);
                })
                .orElseGet(() -> {
                    employment.setClient(c);
                    return employmentRepo.save(employment);
                });
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
