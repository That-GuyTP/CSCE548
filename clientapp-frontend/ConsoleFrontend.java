
import java.util.List;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

public class ConsoleFrontend {

    private static final String BASE = "http://localhost:8080/api";
    private static final HttpClient HTTP = HttpClient.newHttpClient();


    public static void main(String[] args) throws Exception {
        try (Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                printMenu();
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1" -> createClient(sc);
                    case "2" -> getClient(sc);
                    case "3" -> listClients();
                    case "4" -> updateClient(sc);
                    case "5" -> deleteClient(sc);

                    case "6" -> upsertPerson(sc);
                    case "7" -> getPerson(sc);
                    case "8" -> deletePerson(sc);

                    case "9" -> upsertEmployment(sc);
                    case "10" -> getEmployment(sc);
                    case "11" -> deleteEmployment(sc);

                    case "0" -> { System.out.println("Bye."); return; }
                    default -> System.out.println("Invalid.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Console Frontend (calls REST) ===");
        System.out.println("Client:");
        System.out.println("  1) Create Client");
        System.out.println("  2) Get Client");
        System.out.println("  3) List Clients");
        System.out.println("  4) Update Client");
        System.out.println("  5) Delete Client");
        System.out.println("Person:");
        System.out.println("  6) Upsert Person");
        System.out.println("  7) Get Person");
        System.out.println("  8) Delete Person");
        System.out.println("Employment:");
        System.out.println("  9) Upsert Employment");
        System.out.println("  10) Get Employment");
        System.out.println("  11) Delete Employment");
        System.out.println("  0) Exit");
        System.out.print("Select: ");
    }

    // ---------- Client ----------
    private static void createClient(Scanner sc) throws Exception {
        String fn = prompt(sc, "First name: ");
        String ln = prompt(sc, "Last name: ");
        String emp = prompt(sc, "Employment type (W2/1099/Unemployed): ");

        String json = """
                {"firstName":"%s","lastName":"%s","employment":"%s"}
                """.formatted(escape(fn), escape(ln), escape(emp));

        HttpResponse<String> res = send("POST", BASE + "/clients", json);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void getClient(Scanner sc) throws Exception {
        int id = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("GET", BASE + "/clients/" + id, null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void listClients() throws Exception {
        HttpResponse<String> res = send("GET", BASE + "/clients", null);

        System.out.println("HTTP " + res.statusCode());
        if (res.statusCode() / 100 != 2) {
            System.out.println(res.body());
            return;
        }

        String body = res.body().trim();
        if (body.equals("[]")) {
            System.out.println("(no clients found)");
            return;
        }

        // Very simple pretty print: one JSON object per line
        // Assumes response is a JSON array of objects: [{...},{...}]
        body = body.replace("[", "").replace("]", "").trim();

        System.out.println("+-------------------------------- CLIENTS --------------------------------+");
        String[] objs = body.split("\\},\\s*\\{");
        for (int i = 0; i < objs.length; i++) {
            String obj = objs[i];
            if (!obj.startsWith("{")) obj = "{" + obj;
            if (!obj.endsWith("}")) obj = obj + "}";

            System.out.println("Client #" + (i + 1));
            System.out.println(obj);
            System.out.println("--------------------------------------------------------------------------");
        }
    }

    private static void updateClient(Scanner sc) throws Exception {
        int id = promptInt(sc, "Client ID: ");
        String fn = prompt(sc, "New first name: ");
        String ln = prompt(sc, "New last name: ");
        String emp = prompt(sc, "New employment: ");

        String json = """
                {"firstName":"%s","lastName":"%s","employment":"%s"}
                """.formatted(escape(fn), escape(ln), escape(emp));

        HttpResponse<String> res = send("PUT", BASE + "/clients/" + id, json);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void deleteClient(Scanner sc) throws Exception {
        int id = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("DELETE", BASE + "/clients/" + id, null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    // ---------- Person ----------
    private static void upsertPerson(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        String fn = prompt(sc, "Person first name: ");
        String ln = prompt(sc, "Person last name: ");
        String dob = prompt(sc, "DOB (YYYY-MM-DD): ");
        String addr = prompt(sc, "Address: ");
        String sex = prompt(sc, "Legal sex: ");

        // dateOfBirth as ISO string
        LocalDate.parse(dob);

        String json = """
                {"firstName":"%s","lastName":"%s","dateOfBirth":"%s","address":"%s","legalSex":"%s"}
                """.formatted(escape(fn), escape(ln), escape(dob), escape(addr), escape(sex));

        HttpResponse<String> res = send("PUT", BASE + "/clients/" + clientId + "/person", json);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void getPerson(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("GET", BASE + "/clients/" + clientId + "/person", null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void deletePerson(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("DELETE", BASE + "/clients/" + clientId + "/person", null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    // ---------- Employment ----------
    private static void upsertEmployment(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        String biz = prompt(sc, "Business name: ");
        String pos = prompt(sc, "Position name: ");
        String salaryStr = prompt(sc, "Salary (e.g. 55000.00): ");
        new BigDecimal(salaryStr);

        String json = """
                {"businessName":"%s","positionName":"%s","salary":%s}
                """.formatted(escape(biz), escape(pos), salaryStr);

        HttpResponse<String> res = send("PUT", BASE + "/clients/" + clientId + "/employment", json);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void getEmployment(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("GET", BASE + "/clients/" + clientId + "/employment", null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    private static void deleteEmployment(Scanner sc) throws Exception {
        int clientId = promptInt(sc, "Client ID: ");
        HttpResponse<String> res = send("DELETE", BASE + "/clients/" + clientId + "/employment", null);
        System.out.println(res.statusCode());
        System.out.println(res.body());
    }

    // ---------- HTTP helpers ----------
    private static HttpResponse<String> send(String method, String url, String jsonBody)
            throws IOException, InterruptedException {

        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json");

        if (jsonBody != null) {
            b.header("Content-Type", "application/json");
            b.method(method, HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            b.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return HTTP.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String prompt(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            if (!s.isBlank()) return s;
            System.out.println("Cannot be blank.");
        }
    }

    private static int promptInt(Scanner sc, String label) {
        while (true) {
            System.out.print(label);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Enter a valid integer."); }
        }
    }

    // minimal JSON string escaping
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}