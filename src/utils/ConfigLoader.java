package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    public static Map<String, String> loadConfig(String filePath) {
        Map<String, String> config = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    public static void main(String[] args) {
        String configFilePath = "config.env";
        Map<String, String> config = loadConfig(configFilePath);

        String apiKey = config.get("API_KEY");
        String apiSecret = config.get("API_SECRET");

        System.out.println("API Key: " + apiKey);
        System.out.println("API Secret: " + apiSecret);
    }
}
