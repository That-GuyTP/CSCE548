package com.example.config;

import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;
import com.example.repo.ClientRepository;
import com.example.repo.EmploymentRepository;
import com.example.repo.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("prod")
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class ProdDatabaseSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProdDatabaseSeeder.class);

    private static final String[][] SEED_CLIENTS = {
            {"Ava", "Johnson", "W2"},
            {"Liam", "Smith", "1099"},
            {"Noah", "Williams", "W2"},
            {"Emma", "Brown", "Unemployed"},
            {"Olivia", "Jones", "W2"},
            {"Elijah", "Garcia", "1099"},
            {"Sophia", "Miller", "W2"},
            {"Lucas", "Davis", "W2"},
            {"Mia", "Rodriguez", "1099"},
            {"Charlotte", "Martinez", "W2"},
            {"Amelia", "Hernandez", "W2"},
            {"Ethan", "Lopez", "1099"},
            {"Harper", "Gonzalez", "W2"},
            {"Mason", "Wilson", "W2"},
            {"Evelyn", "Anderson", "Unemployed"},
            {"Logan", "Thomas", "W2"},
            {"Abigail", "Taylor", "1099"},
            {"Alexander", "Moore", "W2"},
            {"Emily", "Jackson", "W2"},
            {"Benjamin", "Martin", "1099"},
            {"Elizabeth", "Lee", "W2"},
            {"Henry", "Perez", "W2"},
            {"Sofia", "Thompson", "W2"},
            {"Jackson", "White", "1099"},
            {"Avery", "Harris", "W2"},
            {"Sebastian", "Sanchez", "W2"},
            {"Ella", "Clark", "Unemployed"},
            {"Daniel", "Ramirez", "W2"},
            {"Scarlett", "Lewis", "1099"},
            {"Matthew", "Robinson", "W2"},
            {"Victoria", "Walker", "W2"},
            {"Joseph", "Young", "1099"},
            {"Aria", "Allen", "W2"},
            {"Samuel", "King", "W2"},
            {"Grace", "Wright", "W2"},
            {"David", "Scott", "1099"},
            {"Chloe", "Torres", "W2"},
            {"Owen", "Nguyen", "W2"},
            {"Penelope", "Hill", "Unemployed"},
            {"Wyatt", "Flores", "W2"},
            {"Riley", "Green", "1099"},
            {"John", "Adams", "W2"},
            {"Lily", "Nelson", "W2"},
            {"Gabriel", "Baker", "1099"},
            {"Hannah", "Hall", "W2"},
            {"Carter", "Rivera", "W2"},
            {"Zoey", "Campbell", "W2"},
            {"Isaac", "Mitchell", "1099"},
            {"Nora", "Carter", "W2"},
            {"Julian", "Roberts", "W2"}
    };

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final EmploymentRepository employmentRepository;

    public ProdDatabaseSeeder(ClientRepository clientRepository,
                              PersonRepository personRepository,
                              EmploymentRepository employmentRepository) {
        this.clientRepository = clientRepository;
        this.personRepository = personRepository;
        this.employmentRepository = employmentRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        long existingClients = clientRepository.count();
        if (existingClients > 0) {
            log.info("Skipping production seed. Existing clients found: {}", existingClients);
            return;
        }

        for (String[] row : SEED_CLIENTS) {
            Client client = clientRepository.save(new Client(row[0], row[1], row[2]));
            int clientId = client.getClientId();

            Person person = new Person(
                    row[0],
                    row[1],
                    LocalDate.of(1975, 1, 1).plusDays(clientId % 15000L),
                    String.format("%d Main St, Columbia, SC %05d", 100 + (clientId % 9000), 29000 + (clientId % 999)),
                    (clientId % 2 == 0) ? "Female" : "Male"
            );
            person.setClient(client);
            personRepository.save(person);

            Employment employment = new Employment(
                    companyNameFor(clientId),
                    positionNameFor(clientId),
                    BigDecimal.valueOf(40000L + (clientId % 35L) * 1750L).setScale(2)
            );
            employment.setClient(client);
            employmentRepository.save(employment);
        }

        log.info("Production seed complete. Inserted {} client/person/employment records.", SEED_CLIENTS.length);
    }

    private String companyNameFor(int clientId) {
        return switch (clientId % 10) {
            case 0 -> "Palmetto Tech LLC";
            case 1 -> "Garnet Health Group";
            case 2 -> "Midlands Logistics";
            case 3 -> "Carolina Retail Co.";
            case 4 -> "Congaree Finance";
            case 5 -> "Blue Ridge Energy";
            case 6 -> "Soda City Consulting";
            case 7 -> "Riverwalk Hospitality";
            case 8 -> "Sandhills Manufacturing";
            default -> "Capital City Services";
        };
    }

    private String positionNameFor(int clientId) {
        return switch (clientId % 8) {
            case 0 -> "Analyst";
            case 1 -> "Developer";
            case 2 -> "Manager";
            case 3 -> "Technician";
            case 4 -> "Associate";
            case 5 -> "Coordinator";
            case 6 -> "Administrator";
            default -> "Specialist";
        };
    }
}
