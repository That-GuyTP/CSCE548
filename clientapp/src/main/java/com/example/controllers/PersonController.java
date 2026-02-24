package com.example.controllers;

import com.example.model.Person;
import com.example.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients/{clientId}/person")
public class PersonController {

    private final ClientService service;

    public PersonController(ClientService service) {
        this.service = service;
    }

    // UPSERT Person
    @PutMapping
    public ResponseEntity<Person> upsertPerson(@PathVariable Integer clientId,
                                               @RequestBody Person person) {
        Person saved = service.upsertPerson(clientId, person);
        return ResponseEntity.ok(saved);
    }

    // READ Person
    @GetMapping
    public ResponseEntity<Person> getPerson(@PathVariable Integer clientId) {
        return service.getPerson(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE Person
    @DeleteMapping
    public ResponseEntity<Void> deletePerson(@PathVariable Integer clientId) {
        service.deletePerson(clientId);
        return ResponseEntity.noContent().build();
    }
}