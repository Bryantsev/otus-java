package ru.otus.lesson19.config;

import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

    private static String adminUser;
    private static String adminPassword;
    private static Integer adminSessionExpireInterval;
    private static Integer serverPort;

    public static void loadProperties() {
        Properties serverProperties = new Properties();
        try {
            try (final InputStream stream = ServerProperties.class.getResourceAsStream("/server.properties")) {
                serverProperties.load(stream);
            }
            adminUser = serverProperties.getProperty("adminUser");
            adminPassword = serverProperties.getProperty("adminPassword");
            adminSessionExpireInterval = Integer.parseInt(serverProperties.getProperty("adminSessionExpireInterval", "30"));
            serverPort = Integer.parseInt(serverProperties.getProperty("serverPort", "8080"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getAdminUser() {
        return adminUser;
    }

    public static String getAdminPassword() {
        return adminPassword;
    }

    public static int getAdminSessionExpireInterval() {
        return adminSessionExpireInterval;
    }

    public static int getServerPort() {
        return serverPort;
    }
}
