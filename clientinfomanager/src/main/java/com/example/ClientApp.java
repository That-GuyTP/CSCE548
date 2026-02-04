package com.example;

import com.example.dao.ClientDAO;
import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class ClientApp {

    private static final ClientDAO dao = new ClientDAO();

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== Client DB Console ===");
                System.out.println("1) Client CRUD");
                System.out.println("2) Person CRUD");
                System.out.println("3) Employment CRUD");
                System.out.println("0) Exit");
                System.out.print("Choose: ");

                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> clientMenu(sc);
                    case "2" -> personMenu(sc);
                    case "3" -> employmentMenu(sc);
                    case "0" -> { System.out.println("Goodbye."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }

    // -------------------- CLIENT MENU --------------------

    private static void clientMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Client CRUD ---");
            System.out.println("1) Create Client");
            System.out.println("2) Get Client by ID");
            System.out.println("3) List Clients");
            System.out.println("4) Update Client");
            System.out.println("5) Delete Client");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> createClient(sc);
                case "2" -> getClient(sc);
                case "3" -> dao.listClients().forEach(System.out::println);
                case "4" -> updateClient(sc);
                case "5" -> deleteClient(sc);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createClient(Scanner sc) {
        System.out.print("First name: ");
        String fn = sc.nextLine().trim();
        System.out.print("Last name: ");
        String ln = sc.nextLine().trim();
        System.out.print("Employment (string label): ");
        String emp = sc.nextLine().trim();

        Client c = new Client(null, fn, ln, emp);
        int id = dao.createClient(c);
        System.out.println("Created client with client_id=" + id);
    }

    private static void getClient(Scanner sc) {
        int id = readInt(sc, "Client ID: ");
        Optional<Client> c = dao.getClient(id);
        System.out.println(c.map(Object::toString).orElse("Not found."));
    }

    private static void updateClient(Scanner sc) {
        int id = readInt(sc, "Client ID to update: ");
        Optional<Client> existing = dao.getClient(id);
        if (existing.isEmpty()) {
            System.out.println("Client not found.");
            return;
        }

        System.out.print("New first name: ");
        String fn = sc.nextLine().trim();
        System.out.print("New last name: ");
        String ln = sc.nextLine().trim();
        System.out.print("New employment (string label): ");
        String emp = sc.nextLine().trim();

        Client c = new Client(id, fn, ln, emp);
        System.out.println(dao.updateClient(c) ? "Updated." : "Update failed.");
    }

    private static void deleteClient(Scanner sc) {
        int id = readInt(sc, "Client ID to delete: ");
        System.out.println(dao.deleteClient(id) ? "Deleted." : "Delete failed (not found?).");
        System.out.println("Note: person/employment rows will be deleted automatically via ON DELETE CASCADE.");
    }

    // -------------------- PERSON MENU --------------------

    private static void personMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Person CRUD ---");
            System.out.println("1) Create Person (requires existing Client)");
            System.out.println("2) Get Person by Client ID");
            System.out.println("3) Update Person");
            System.out.println("4) Delete Person");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> createPerson(sc);
                case "2" -> getPerson(sc);
                case "3" -> updatePerson(sc);
                case "4" -> deletePerson(sc);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createPerson(Scanner sc) {
        int clientId = readInt(sc, "Client ID (must exist): ");
        System.out.print("First name: ");
        String fn = sc.nextLine().trim();
        System.out.print("Last name: ");
        String ln = sc.nextLine().trim();
        LocalDate dob = readDate(sc, "Date of birth (YYYY-MM-DD): ");
        System.out.print("Address: ");
        String addr = sc.nextLine().trim();
        System.out.print("Legal sex: ");
        String sex = sc.nextLine().trim();

        Person p = new Person(clientId, fn, ln, dob, addr, sex);
        System.out.println(dao.createPerson(p) ? "Created." : "Create failed.");
    }

    private static void getPerson(Scanner sc) {
        int id = readInt(sc, "Client ID: ");
        System.out.println(dao.getPerson(id).map(Object::toString).orElse("Not found."));
    }

    private static void updatePerson(Scanner sc) {
        int clientId = readInt(sc, "Client ID to update: ");
        if (dao.getPerson(clientId).isEmpty()) {
            System.out.println("Person not found for that client.");
            return;
        }

        System.out.print("New first name: ");
        String fn = sc.nextLine().trim();
        System.out.print("New last name: ");
        String ln = sc.nextLine().trim();
        LocalDate dob = readDate(sc, "New date of birth (YYYY-MM-DD): ");
        System.out.print("New address: ");
        String addr = sc.nextLine().trim();
        System.out.print("New legal sex: ");
        String sex = sc.nextLine().trim();

        Person p = new Person(clientId, fn, ln, dob, addr, sex);
        System.out.println(dao.updatePerson(p) ? "Updated." : "Update failed.");
    }

    private static void deletePerson(Scanner sc) {
        int clientId = readInt(sc, "Client ID to delete person row: ");
        System.out.println(dao.deletePerson(clientId) ? "Deleted." : "Delete failed (not found?).");
    }

    // -------------------- EMPLOYMENT MENU --------------------

    private static void employmentMenu(Scanner sc) {
        while (true) {
            System.out.println("\n--- Employment CRUD ---");
            System.out.println("1) Create Employment (requires existing Client)");
            System.out.println("2) Get Employment by Client ID");
            System.out.println("3) Update Employment");
            System.out.println("4) Delete Employment");
            System.out.println("0) Back");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> createEmployment(sc);
                case "2" -> getEmployment(sc);
                case "3" -> updateEmployment(sc);
                case "4" -> deleteEmployment(sc);
                case "0" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void createEmployment(Scanner sc) {
        int clientId = readInt(sc, "Client ID (must exist): ");
        System.out.print("Business name: ");
        String business = sc.nextLine().trim();
        System.out.print("Position name: ");
        String position = sc.nextLine().trim();
        BigDecimal salary = readMoney(sc, "Salary (e.g., 55000.00): ");

        Employment e = new Employment(clientId, business, position, salary);
        System.out.println(dao.createEmployment(e) ? "Created." : "Create failed.");
    }

    private static void getEmployment(Scanner sc) {
        int id = readInt(sc, "Client ID: ");
        System.out.println(dao.getEmployment(id).map(Object::toString).orElse("Not found."));
    }

    private static void updateEmployment(Scanner sc) {
        int clientId = readInt(sc, "Client ID to update: ");
        if (dao.getEmployment(clientId).isEmpty()) {
            System.out.println("Employment not found for that client.");
            return;
        }

        System.out.print("New business name: ");
        String business = sc.nextLine().trim();
        System.out.print("New position name: ");
        String position = sc.nextLine().trim();
        BigDecimal salary = readMoney(sc, "New salary: ");

        Employment e = new Employment(clientId, business, position, salary);
        System.out.println(dao.updateEmployment(e) ? "Updated." : "Update failed.");
    }

    private static void deleteEmployment(Scanner sc) {
        int clientId = readInt(sc, "Client ID to delete employment row: ");
        System.out.println(dao.deleteEmployment(clientId) ? "Deleted." : "Delete failed (not found?).");
    }

    // -------------------- INPUT HELPERS --------------------

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static LocalDate readDate(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                System.out.println("Please use format YYYY-MM-DD.");
            }
        }
    }

    private static BigDecimal readMoney(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return new BigDecimal(s);
            } catch (Exception e) {
                System.out.println("Please enter a valid decimal number (e.g., 55000.00).");
            }
        }
    }
}