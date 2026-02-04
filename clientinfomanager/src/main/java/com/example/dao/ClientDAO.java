package com.example.dao;

import com.example.model.Client;
import com.example.model.Employment;
import com.example.model.Person;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {

    // -------------------- CLIENT CRUD --------------------

    public int createClient(Client c) {
        String sql = "INSERT INTO client (first_name, last_name, employment) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmployment());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setClientId(id);
                    return id;
                }
            }
            throw new SQLException("No generated key returned for client insert.");
        } catch (SQLException e) {
            throw new RuntimeException("createClient failed", e);
        }
    }

    public Optional<Client> getClient(int clientId) {
        String sql = "SELECT client_id, first_name, last_name, employment FROM client WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapClient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getClient failed", e);
        }
    }

    public List<Client> listClients() {
        String sql = "SELECT client_id, first_name, last_name, employment FROM client ORDER BY client_id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Client> out = new ArrayList<>();
            while (rs.next()) out.add(mapClient(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("listClients failed", e);
        }
    }

    public boolean updateClient(Client c) {
        if (c.getClientId() == null) throw new IllegalArgumentException("ClientId required for update.");
        String sql = "UPDATE client SET first_name=?, last_name=?, employment=? WHERE client_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getEmployment());
            ps.setInt(4, c.getClientId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updateClient failed", e);
        }
    }

    public boolean deleteClient(int clientId) {
        // CASCADE deletes person/employment automatically
        String sql = "DELETE FROM client WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("deleteClient failed", e);
        }
    }

    // -------------------- PERSON CRUD --------------------

    public boolean createPerson(Person p) {
        String sql = "INSERT INTO person (client_id, first_name, last_name, date_of_birth, address, legal_sex) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getClientId());
            ps.setString(2, p.getFirstName());
            ps.setString(3, p.getLastName());
            ps.setDate(4, Date.valueOf(p.getDateOfBirth()));
            ps.setString(5, p.getAddress());
            ps.setString(6, p.getLegalSex());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("createPerson failed (make sure client_id exists in client table)", e);
        }
    }

    public Optional<Person> getPerson(int clientId) {
        String sql = "SELECT client_id, first_name, last_name, date_of_birth, address, legal_sex FROM person WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapPerson(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getPerson failed", e);
        }
    }

    public boolean updatePerson(Person p) {
        String sql = "UPDATE person SET first_name=?, last_name=?, date_of_birth=?, address=?, legal_sex=? WHERE client_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setDate(3, Date.valueOf(p.getDateOfBirth()));
            ps.setString(4, p.getAddress());
            ps.setString(5, p.getLegalSex());
            ps.setInt(6, p.getClientId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("updatePerson failed", e);
        }
    }

    public boolean deletePerson(int clientId) {
        String sql = "DELETE FROM person WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("deletePerson failed", e);
        }
    }

    // -------------------- EMPLOYMENT CRUD --------------------

    public boolean createEmployment(Employment e) {
        String sql = "INSERT INTO employment (client_id, business_name, position_name, salary) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, e.getClientId());
            ps.setString(2, e.getBusinessName());
            ps.setString(3, e.getPositionName());
            ps.setBigDecimal(4, e.getSalary());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new RuntimeException("createEmployment failed (make sure client_id exists in client table)", ex);
        }
    }

    public Optional<Employment> getEmployment(int clientId) {
        String sql = "SELECT client_id, business_name, position_name, salary FROM employment WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapEmployment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("getEmployment failed", e);
        }
    }

    public boolean updateEmployment(Employment e) {
        String sql = "UPDATE employment SET business_name=?, position_name=?, salary=? WHERE client_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getBusinessName());
            ps.setString(2, e.getPositionName());
            ps.setBigDecimal(3, e.getSalary());
            ps.setInt(4, e.getClientId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new RuntimeException("updateEmployment failed", ex);
        }
    }

    public boolean deleteEmployment(int clientId) {
        String sql = "DELETE FROM employment WHERE client_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("deleteEmployment failed", e);
        }
    }

    // -------------------- MAPPERS --------------------

    private Client mapClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("client_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("employment")
        );
    }

    private Person mapPerson(ResultSet rs) throws SQLException {
        LocalDate dob = rs.getDate("date_of_birth").toLocalDate();
        return new Person(
                rs.getInt("client_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                dob,
                rs.getString("address"),
                rs.getString("legal_sex")
        );
    }

    private Employment mapEmployment(ResultSet rs) throws SQLException {
        BigDecimal salary = rs.getBigDecimal("salary");
        return new Employment(
                rs.getInt("client_id"),
                rs.getString("business_name"),
                rs.getString("position_name"),
                salary
        );
    }
}