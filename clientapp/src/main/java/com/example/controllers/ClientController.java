package com.example.controllers;

import com.example.model.Client;
import com.example.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    // CREATE Client
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client saved = service.createClient(client);
        return ResponseEntity
                .created(URI.create("/api/clients/" + saved.getClientId()))
                .body(saved);
    }

    // READ Client
    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClient(@PathVariable Integer clientId) {
        return service.getClient(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // LIST Clients
    @GetMapping
    public List<Client> listClients() {
        return service.getAllClients();
    }

    // UPDATE Client (maps to service.updateClient)
    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer clientId,
                                               @RequestBody ClientUpdateRequest req) {
        Client updated = service.updateClient(clientId, req.firstName(), req.lastName(), req.employment());
        return ResponseEntity.ok(updated);
    }

    // DELETE Client
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer clientId) {
        service.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }

    // Simple request DTO for update
    public record ClientUpdateRequest(String firstName, String lastName, String employment) { }
}