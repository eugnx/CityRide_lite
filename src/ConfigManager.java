import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;


// Manages all mutable system configuration: base fares, passenger discounts,
// daily caps, and peak time windows.
// Loads from config/config.json on first access; falls back to CityRideDataset
// defaults if the file is missing or corrupt.

public final class ConfigManager {

    private static final String CONFIG_PATH = "config/config.json";
    private static ConfigManager instance;

    private Map<String, BigDecimal> baseFares;
    private Map<CityRideDataset.PassengerType, BigDecimal> discountRates;
    private Map<CityRideDataset.PassengerType, BigDecimal> dailyCaps;

    // Peak window: hour range [peakStartHour, peakEndHour)  e.g. 7-9 and 17-19
    private int morningPeakStart;
    private int morningPeakEnd;
    private int eveningPeakStart;
    private int eveningPeakEnd;


    // --- Singleton access ---

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            instance.load();
        }
        return instance;
    }


    private ConfigManager() {}


    // --- Load / Save ---


    // Loads config from file. Falls back to dataset defaults if file is missing.

    public void load() {
        Path path = Paths.get(CONFIG_PATH);
        if (Files.exists(path)) {
            try {
                String json = Files.readString(path);
                parseJson(json);
                System.out.println("Configuration loaded from " + CONFIG_PATH);
                return;
            } catch (Exception e) {
                System.out.println("Warning: Could not read config file (" + e.getMessage() +
                        "). Starting with defaults.");
            }
        } else {
            System.out.println("No config file found at " + CONFIG_PATH + ". Starting with safe defaults.");
        }
        loadDefaults();
    }


    // Saves the current in-memory configuration to config/config.json.

    public boolean save() {
        try {
            Files.createDirectories(Paths.get("config"));
            Files.writeString(Paths.get(CONFIG_PATH), toJson());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
            return false;
        }
    }


    // --- Defaults from CityRideDataset ---

    private void loadDefaults() {
        // Copy fares from the dataset constants
        baseFares = new HashMap<>(CityRideDataset.BASE_FARE);

        discountRates = new HashMap<>();
        for (CityRideDataset.PassengerType t : CityRideDataset.PassengerType.values()) {
            discountRates.put(t, CityRideDataset.DISCOUNT_RATE.get(t));
        }

        dailyCaps = new HashMap<>();
        for (CityRideDataset.PassengerType t : CityRideDataset.PassengerType.values()) {
            dailyCaps.put(t, CityRideDataset.DAILY_CAP.get(t));
        }

        // Default peak windows: 07:00-09:00 and 17:00-19:00
        morningPeakStart  = 7;
        morningPeakEnd    = 9;
        eveningPeakStart  = 17;
        eveningPeakEnd    = 19;
    }


    // --- Fare access ---

    public BigDecimal getBaseFare(int fromZone, int toZone, CityRideDataset.TimeBand band) {
        String key = CityRideDataset.key(fromZone, toZone, band);
        BigDecimal fare = baseFares.get(key);
        return fare != null ? fare : BigDecimal.ZERO;
    }

    public void setBaseFare(int fromZone, int toZone, CityRideDataset.TimeBand band, BigDecimal fare) {
        baseFares.put(CityRideDataset.key(fromZone, toZone, band), fare.setScale(2, RoundingMode.HALF_UP));
    }

    public void deleteBaseFare(int fromZone, int toZone, CityRideDataset.TimeBand band) {
        baseFares.remove(CityRideDataset.key(fromZone, toZone, band));
    }


    // --- Discount access ---

    public BigDecimal getDiscountRate(CityRideDataset.PassengerType type) {
        return discountRates.getOrDefault(type, BigDecimal.ZERO);
    }

    public void setDiscountRate(CityRideDataset.PassengerType type, BigDecimal rate) {
        discountRates.put(type, rate.setScale(2, RoundingMode.HALF_UP));
    }

    public void deleteDiscountRate(CityRideDataset.PassengerType type) {
        discountRates.put(type, BigDecimal.ZERO); // reset to 0 rather than remove
    }


    // --- Daily cap access ---

    public BigDecimal getDailyCap(CityRideDataset.PassengerType type) {
        return dailyCaps.getOrDefault(type, new BigDecimal("999.00"));
    }

    public void setDailyCap(CityRideDataset.PassengerType type, BigDecimal cap) {
        dailyCaps.put(type, cap.setScale(2, RoundingMode.HALF_UP));
    }

    public void deleteDailyCap(CityRideDataset.PassengerType type) {
        dailyCaps.put(type, new BigDecimal("999.00")); // effectively uncapped
    }


    // --- Peak window access ---

    public int getMorningPeakStart()  { return morningPeakStart; }
    public int getMorningPeakEnd()    { return morningPeakEnd; }
    public int getEveningPeakStart()  { return eveningPeakStart; }
    public int getEveningPeakEnd()    { return eveningPeakEnd; }

    public void setMorningPeak(int start, int end) {
        morningPeakStart = start;
        morningPeakEnd   = end;
    }

    public void setEveningPeak(int start, int end) {
        eveningPeakStart = start;
        eveningPeakEnd   = end;
    }


    // Returns all base fares as a copy (for admin display).

    public Map<String, BigDecimal> getAllBaseFares() {
        return new HashMap<>(baseFares);
    }

    public Map<CityRideDataset.PassengerType, BigDecimal> getAllDiscountRates() {
        return new HashMap<>(discountRates);
    }

    public Map<CityRideDataset.PassengerType, BigDecimal> getAllDailyCaps() {
        return new HashMap<>(dailyCaps);
    }


    // --- JSON serialisation (hand-rolled to avoid dependencies) ---

    private String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        // base fares
        sb.append("  \"baseFares\": {\n");
        int fareCount = 0;
        for (Map.Entry<String, BigDecimal> e : baseFares.entrySet()) {
            if (fareCount++ > 0) sb.append(",\n");
            sb.append("    \"").append(e.getKey()).append("\": ")
                    .append(e.getValue().toPlainString());
        }
        sb.append("\n  },\n");

        // discount rates
        sb.append("  \"discountRates\": {\n");
        int dCount = 0;
        for (Map.Entry<CityRideDataset.PassengerType, BigDecimal> e : discountRates.entrySet()) {
            if (dCount++ > 0) sb.append(",\n");
            sb.append("    \"").append(e.getKey().name()).append("\": ")
                    .append(e.getValue().toPlainString());
        }
        sb.append("\n  },\n");

        // daily caps
        sb.append("  \"dailyCaps\": {\n");
        int cCount = 0;
        for (Map.Entry<CityRideDataset.PassengerType, BigDecimal> e : dailyCaps.entrySet()) {
            if (cCount++ > 0) sb.append(",\n");
            sb.append("    \"").append(e.getKey().name()).append("\": ")
                    .append(e.getValue().toPlainString());
        }
        sb.append("\n  },\n");

        // peak windows
        sb.append("  \"peakWindows\": {\n");
        sb.append("    \"morningStart\": ").append(morningPeakStart).append(",\n");
        sb.append("    \"morningEnd\": ").append(morningPeakEnd).append(",\n");
        sb.append("    \"eveningStart\": ").append(eveningPeakStart).append(",\n");
        sb.append("    \"eveningEnd\": ").append(eveningPeakEnd).append("\n");
        sb.append("  }\n");

        sb.append("}");
        return sb.toString();
    }

    private void parseJson(String json) {
        loadDefaults(); // start from defaults then overwrite with file values

        // Parse baseFares block
        String fareBlock = extractBlock(json, "baseFares");
        if (fareBlock != null) {
            for (String line : fareBlock.split(",")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim().replace("\"", "");
                    try {
                        BigDecimal val = new BigDecimal(parts[1].trim());
                        baseFares.put(key, val);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        // Parse discountRates block
        String discBlock = extractBlock(json, "discountRates");
        if (discBlock != null) {
            for (String line : discBlock.split(",")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String typeName = parts[0].trim().replace("\"", "");
                    try {
                        CityRideDataset.PassengerType t = CityRideDataset.PassengerType.valueOf(typeName);
                        discountRates.put(t, new BigDecimal(parts[1].trim()));
                    } catch (Exception ignored) {}
                }
            }
        }

        // Parse dailyCaps block
        String capBlock = extractBlock(json, "dailyCaps");
        if (capBlock != null) {
            for (String line : capBlock.split(",")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String typeName = parts[0].trim().replace("\"", "");
                    try {
                        CityRideDataset.PassengerType t = CityRideDataset.PassengerType.valueOf(typeName);
                        dailyCaps.put(t, new BigDecimal(parts[1].trim()));
                    } catch (Exception ignored) {}
                }
            }
        }

        // Parse peakWindows block
        String peakBlock = extractBlock(json, "peakWindows");
        if (peakBlock != null) {
            morningPeakStart  = parseIntField(peakBlock, "morningStart",  morningPeakStart);
            morningPeakEnd    = parseIntField(peakBlock, "morningEnd",    morningPeakEnd);
            eveningPeakStart  = parseIntField(peakBlock, "eveningStart",  eveningPeakStart);
            eveningPeakEnd    = parseIntField(peakBlock, "eveningEnd",    eveningPeakEnd);
        }
    }

    // Extracts the content inside a named JSON object block.
    private String extractBlock(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx == -1) return null;
        int open = json.indexOf('{', keyIdx + search.length());
        if (open == -1) return null;
        int depth = 0;
        for (int i = open; i < json.length(); i++) {
            if (json.charAt(i) == '{') depth++;
            else if (json.charAt(i) == '}') {
                depth--;
                if (depth == 0) return json.substring(open + 1, i).trim();
            }
        }
        return null;
    }

    private int parseIntField(String block, String fieldName, int defaultVal) {
        String search = "\"" + fieldName + "\"";
        int idx = block.indexOf(search);
        if (idx == -1) return defaultVal;
        int colon = block.indexOf(':', idx + search.length());
        if (colon == -1) return defaultVal;
        int start = colon + 1;
        int end = start;
        while (end < block.length() && (Character.isDigit(block.charAt(end)) || block.charAt(end) == ' ')) end++;
        try {
            return Integer.parseInt(block.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
