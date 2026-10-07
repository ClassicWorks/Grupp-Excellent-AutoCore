package com.wac.autocore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslationService {

    private static final String URL_PROPERTY = "translate.url";
    private static final String DEFAULT_URL = "http://localhost:5000/translate";
    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int READ_TIMEOUT_MS = 15000;

    public static String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        if (sourceLang == null || targetLang == null || sourceLang.equals(targetLang)) {
            return null;
        }

        HttpURLConnection connection = null;
        try {
            String body = "q=" + URLEncoder.encode(text, "UTF-8")
                    + "&source=" + URLEncoder.encode(sourceLang, "UTF-8")
                    + "&target=" + URLEncoder.encode(targetLang, "UTF-8")
                    + "&format=text";

            URL url = new URL(System.getProperty(URL_PROPERTY, DEFAULT_URL));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            try (OutputStream out = connection.getOutputStream()) {
                out.write(bytes);
            }

            if (connection.getResponseCode() != 200) {
                System.out.println("Translation failed, HTTP status " + connection.getResponseCode());
                return null;
            }

            try (InputStream in = connection.getInputStream()) {
                return extractTranslatedText(readAll(in));
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Translation unavailable: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String extractTranslatedText(String json) {
        int key = json.indexOf("\"translatedText\"");
        if (key < 0) {
            return null;
        }
        int colon = json.indexOf(':', key);
        if (colon < 0) {
            return null;
        }
        int start = json.indexOf('"', colon + 1);
        if (start < 0) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (int i = start + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"') {
                return result.toString();
            }
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(++i);
                switch (next) {
                    case 'n':
                        result.append('\n');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 'b':
                        result.append('\b');
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case 'u':
                        if (i + 4 < json.length()) {
                            result.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        break;
                    default:
                        result.append(next);
                }
            } else {
                result.append(c);
            }
        }
        return null;
    }
}