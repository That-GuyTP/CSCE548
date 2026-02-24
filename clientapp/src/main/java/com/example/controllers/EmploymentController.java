package com.example.controllers;

import com.example.model.Employment;
import com.example.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients/{clientId}/employment")
public class EmploymentController {

    private final ClientService service;

    public EmploymentController(ClientService service) {
        this.service = service;
    }

    // UPSERT Employment
    @PutMapping
    public ResponseEntity<Employment> upsertEmployment(@PathVariable Integer clientId,
                                                      @RequestBody Employment employment) {
        Employment saved = service.upsertEmployment(clientId, employment);
        return ResponseEntity.ok(saved);
    }

    // READ Employment
    @GetMapping
    public ResponseEntity<Employment> getEmployment(@PathVariable Integer clientId) {
        return service.getEmployment(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE Employment
    @DeleteMapping
    public ResponseEntity<Void> deleteEmployment(@PathVariable Integer clientId) {
        service.deleteEmployment(clientId);
        return ResponseEntity.noContent().build();
    }
}