package com.wac.autocore.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LanguageManager {

    private static Locale currentLocale = new Locale("sv");
    private static ResourceBundle bundle = loadBundle(currentLocale);

    private static ResourceBundle loadBundle(Locale locale) {
        String fileName = "messages_" + locale.getLanguage() + ".properties";
        try (InputStreamReader reader = new InputStreamReader(
                LanguageManager.class.getClassLoader().getResourceAsStream(fileName),
                StandardCharsets.UTF_8)) {
            return new PropertyResourceBundle(reader);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Could not load language file: " + fileName, e);
        }
    }

    public static String getString(String key) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return key;
    }

    public static void setLanguage(String languageCode) {
        currentLocale = new Locale(languageCode);
        bundle = loadBundle(currentLocale);
    }

    public static String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }

    public static void toggleLanguage() {
        if (currentLocale.getLanguage().equals("sv")) {
            setLanguage("en");
        } else {
            setLanguage("sv");
        }
    }
}