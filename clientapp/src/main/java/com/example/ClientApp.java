package com.example;

import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;
import com.example.service.ClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class ClientApp /* implements CommandLineRunner */ {

    private final ClientService service;

    public ClientApp(ClientService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }

    /* Project 1 Command Line
    @ Override
    public void run(String... args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                String choice = sc.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> createClient(sc);
                        case "2" -> readClient(sc);
                        case "3" -> updateClient(sc);
                        case "4" -> deleteClient(sc);
                        case "5" -> listClients();

                        case "6" -> upsertPerson(sc);
                        case "7" -> readPerson(sc);
                        case "8" -> deletePerson(sc);

                        case "9" -> upsertEmployment(sc);
                        case "10" -> readEmployment(sc);
                        case "11" -> deleteEmployment(sc);

                        case "0" -> {
                            System.out.println("Bye.");
                            return;
                        }
                        default -> System.out.println("Invalid option.");
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }
    } */

    private void printMenu() {
        System.out.println("\n==== Client Console ====");
        System.out.println("Client CRUD");
        System.out.println("  1) Create Client");
        System.out.println("  2) Read Client");
        System.out.println("  3) Update Client");
        System.out.println("  4) Delete Client");
        System.out.println("  5) List Clients");
        System.out.println("Person CRUD (by client_id)");
        System.out.println("  6) Upsert Person for Client");
        System.out.println("  7) Read Person for Client");
        System.out.println("  8) Delete Person for Client");
        System.out.println("Employment CRUD (by client_id)");
        System.out.println("  9) Upsert Employment for Client");
        System.out.println("  10) Read Employment for Client");
        System.out.println("  11) Delete Employment for Client");
        System.out.println("  0) Exit");
        System.out.print("Select: ");
    }

    // --------------------------
    // Client CRUD
    // --------------------------

    private void createClient(Scanner sc) {
        String fn = promptNonBlank(sc, "Client first name: ");
        String ln = promptNonBlank(sc, "Client last name: ");
        String empType = promptNonBlank(sc, "Client employment (e.g., W2/1099/Unemployed): ");

        Client saved = service.createClient(new Client(fn, ln, empType));
        System.out.println("Created: " + saved);
    }

    private void readClient(Scanner sc) {
        Integer id = promptInt(sc, "Client ID: ");
        Optional<Client> c = service.getClient(id);
        System.out.println(c.map(Object::toString).orElse("Not found."));
    }

    private void updateClient(Scanner sc) {
        Integer id = promptInt(sc, "Client ID: ");
        String fn = promptNonBlank(sc, "New first name: ");
        String ln = promptNonBlank(sc, "New last name: ");
        String empType = promptNonBlank(sc, "New employment (e.g., W2/1099/Unemployed): ");

        Client updated = service.updateClient(id, fn, ln, empType);
        System.out.println("Updated: " + updated);
    }

    private void deleteClient(Scanner sc) {
        Integer id = promptInt(sc, "Client ID: ");
        service.deleteClient(id);
        System.out.println("Deleted client " + id);
    }

    private void listClients() {
        service.getAllClients().forEach(System.out::println);
    }

    // --------------------------
    // Person CRUD (by client_id)
    // --------------------------

    private void upsertPerson(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID (person.client_id): ");

        String fn = promptNonBlank(sc, "Person first name: ");
        String ln = promptNonBlank(sc, "Person last name: ");
        LocalDate dob = promptDate(sc, "Date of birth (YYYY-MM-DD): ");
        String addr = promptNonBlank(sc, "Address: ");
        String sex = promptNonBlank(sc, "Legal sex: ");

        Person p = new Person(fn, ln, dob, addr, sex);
        Person saved = service.upsertPerson(clientId, p);
        System.out.println("Saved: " + saved);
    }

    private void readPerson(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID: ");
        System.out.println(service.getPerson(clientId).map(Object::toString).orElse("Not found."));
    }

    private void deletePerson(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID: ");
        service.deletePerson(clientId);
        System.out.println("Deleted person for client " + clientId);
    }

    // --------------------------
    // Employment CRUD (by client_id)
    // --------------------------

    private void upsertEmployment(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID (employment.client_id): ");

        String businessName = promptNonBlank(sc, "Business name: ");
        String positionName = promptNonBlank(sc, "Position name: ");
        BigDecimal salary = promptMoney(sc, "Salary (e.g., 55000.00): ");

        Employment emp = new Employment(businessName, positionName, salary);
        Employment saved = service.upsertEmployment(clientId, emp);
        System.out.println("Saved: " + saved);
    }

    private void readEmployment(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID: ");
        System.out.println(service.getEmployment(clientId).map(Object::toString).orElse("Not found."));
    }

    private void deleteEmployment(Scanner sc) {
        Integer clientId = promptInt(sc, "Client ID: ");
        service.deleteEmployment(clientId);
        System.out.println("Deleted employment for client " + clientId);
    }

    // --------------------------
    // Input helpers
    // --------------------------

    private Integer promptInt(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private String promptNonBlank(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            if (!s.isBlank()) return s;
            System.out.println("Value cannot be blank.");
        }
    }

    private LocalDate promptDate(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD.");
            }
        }
    }

    private BigDecimal promptMoney(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Example: 55000.00");
            }
        }
    }
}