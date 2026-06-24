import java.io.*;
import java.nio.file.*;


public class ProfileFileHandler {

    private static final String PROFILE_DIR = "profiles/";
    private static final String FILE_EXTENSION = ".json";


     // Saves a RiderProfile to a JSON file named after the rider.

    public boolean saveProfile(RiderProfile profile) {
        try {
            Files.createDirectories(Paths.get(PROFILE_DIR));
            String filename = PROFILE_DIR + sanitiseFilename(profile.getName()) + FILE_EXTENSION;
            String json = serialiseProfile(profile);
            Files.writeString(Paths.get(filename), json);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving profile: " + e.getMessage());
            return false;
        }
    }

     // Loads a RiderProfile from a JSON file by rider name.

    public RiderProfile loadProfile(String name) {
        String filename = PROFILE_DIR + sanitiseFilename(name) + FILE_EXTENSION;
        Path path = Paths.get(filename);

        if (!Files.exists(path)) {
            System.out.println("No profile found for: " + name);
            return null;
        }

        try {
            String json = Files.readString(path);
            return deserialiseProfile(json);
        } catch (IOException e) {
            System.out.println("Error loading profile: " + e.getMessage());
            return null;
        }
    }


     // Lists all saved profile names by scanning the profiles directory.

    public String[] listSavedProfiles() {
        File dir = new File(PROFILE_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }
        File[] files = dir.listFiles((d, n) -> n.endsWith(FILE_EXTENSION));
        if (files == null || files.length == 0) {
            return new String[0];
        }
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName().replace(FILE_EXTENSION, "");
        }
        return names;
    }


    // Private helpers



     //Serialises a RiderProfile to a JSON string.

    private String serialiseProfile(RiderProfile profile) {
        return "{\n" +
                "  \"name\": \"" + escapeJson(profile.getName()) + "\",\n" +
                "  \"passengerType\": \"" + profile.getPassengerType().name() + "\",\n" +
                "  \"defaultPaymentOption\": \"" + profile.getDefaultPaymentOption().name() + "\"\n" +
                "}";
    }


     //Deserialises a RiderProfile from a simple JSON string.

    private RiderProfile deserialiseProfile(String json) {
        String name = extractJsonValue(json, "name");
        String passengerTypeStr = extractJsonValue(json, "passengerType");
        String paymentOptionStr = extractJsonValue(json, "defaultPaymentOption");

        if (name == null || passengerTypeStr == null || paymentOptionStr == null) {
            System.out.println("Error: Profile file is missing required fields.");
            return null;
        }

        CityRideDataset.PassengerType passengerType;
        try {
            passengerType = CityRideDataset.PassengerType.valueOf(passengerTypeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Unknown passenger type in profile: " + passengerTypeStr);
            return null;
        }

        PaymentOption paymentOption;
        try {
            paymentOption = PaymentOption.valueOf(paymentOptionStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Unknown payment option in profile: " + paymentOptionStr);
            return null;
        }

        return new RiderProfile(name, passengerType, paymentOption);
    }


     // Extracts the string value for a given key from a simple JSON object.

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) {
            return null;
        }
        int colonIndex = json.indexOf(":", keyIndex + searchKey.length());
        if (colonIndex == -1) {
            return null;
        }
        int openQuote = json.indexOf("\"", colonIndex + 1);
        if (openQuote == -1) {
            return null;
        }
        int closeQuote = json.indexOf("\"", openQuote + 1);
        if (closeQuote == -1) {
            return null;
        }
        return json.substring(openQuote + 1, closeQuote);
    }


    private String sanitiseFilename(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase();
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}