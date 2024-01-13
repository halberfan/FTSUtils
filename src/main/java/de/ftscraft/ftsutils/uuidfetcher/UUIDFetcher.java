package de.ftscraft.ftsutils.uuidfetcher;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDFetcher {

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String NAME_URL = "https://api.mojang.com/user/profile/%s";

    private static final Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private static final Map<String, String> nameCache = new HashMap<String, String>();

    public static UUID getUUID(String username) {
        if (uuidCache.containsKey(username))
            return uuidCache.get(username);
        try {
            URL url = new URL(String.format(UUID_URL, username));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setze die Anfrage-Methode auf GET
            connection.setRequestMethod("GET");

            // Lese die Antwort (Response) ein
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Schließe die Verbindung
            connection.disconnect();

            // Verarbeite die JSON-Antwort mit Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Extrahiere den "name" aus der JSON-Antwort
            String uuid = jsonObject.get("id").getAsString();
            String formattedUUID = uuid.substring(0, 8) + "-" +
                    uuid.substring(8, 12) + "-" +
                    uuid.substring(12, 16) + "-" +
                    uuid.substring(16, 20) + "-" +
                    uuid.substring(20);
            UUID r = UUID.fromString(formattedUUID);
            uuidCache.put(username, r);
            return r;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getName(UUID uuid) {
        return getName(uuid.toString());
    }

    public static String getName(String uuid) {
        if (nameCache.containsKey(uuid))
            return nameCache.get(uuid);
        try {
            URL url = new URL(String.format(NAME_URL, uuid.replace("-", "")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Setze die Anfrage-Methode auf GET
            connection.setRequestMethod("GET");

            // Lese die Antwort (Response) ein
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Schließe die Verbindung
            connection.disconnect();

            // Verarbeite die JSON-Antwort mit Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

            // Extrahiere den "name" aus der JSON-Antwort
            String playerName = jsonObject.get("name").getAsString();
            nameCache.put(uuid, playerName);
            return playerName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
