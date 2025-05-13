package com.wipro.cloudcareai.sdmd_genai_server.config.context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ContextLoader {

    public static String loadContext(String filename) {
        try (InputStream is = ContextLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new RuntimeException("Context file not found: " + filename);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load context from file: " + filename, e);
        }
    }
}
