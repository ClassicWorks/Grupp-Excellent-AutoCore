package com.wac.autocore.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final String DB_PASSWORD_VARIABLE = "AUTOCORE_DB_PASSWORD";

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        String password = System.getenv(DB_PASSWORD_VARIABLE);
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Database password missing. Set the environment variable " + DB_PASSWORD_VARIABLE);
        }

        return new Configuration()
                .configure()
                .setProperty("hibernate.connection.password", password)
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}