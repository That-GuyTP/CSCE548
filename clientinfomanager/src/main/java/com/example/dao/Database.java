package com.example.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class Database {
    private static final String PROPS_FILE = "db.properties";
    private static final Properties PROPS = loadProps();

    private Database() {}

    private static Properties loadProps() {
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + PROPS_FILE + " in src/main/resources");
            }
            Properties p = new Properties();
            p.load(in);
            return p;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    PROPS.getProperty("db.url"),
                    PROPS.getProperty("db.user"),
                    PROPS.getProperty("db.password")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to DB", e);
        }
    }
}