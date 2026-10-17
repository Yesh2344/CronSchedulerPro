package com.example.cron.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration loader.
 * Loads properties from {@code application.properties} and merges
 * environment variable overrides.
 */
public final class AppConfig {

    private static final String PROPERTIES_FILE = "/application.properties";
    private static AppConfig instance;
    private final Properties properties = new Properties();

    private AppConfig() {
        loadFromFile();
        overrideWithEnv();
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadFromFile() {
        try (InputStream is = getClass().getResourceAsStream(PROPERTIES_FILE)) {
            if (is != null) {
                properties.load(is);
            } else {
                System.err.println("Configuration file not found: " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    private void overrideWithEnv() {
        // Environment variables are uppercase with underscores.
        // Convert them back to property style (dots).
        System.getenv().forEach((envKey, envValue) -> {
            String propKey = envKey.toLowerCase().replace('_', '.');
            if (properties.containsKey(propKey)) {
                properties.setProperty(propKey, envValue);
            }
        });
    }

    /**
     * Retrieves a property value.
     *
     * @param key property key
     * @return property value or {@code null}
     */
    public String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves a property value with a default.
     *
     * @param key          property key
     * @param defaultValue default value if key is missing
     * @return property value or {@code defaultValue}
     */
    public String getOrDefault(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}