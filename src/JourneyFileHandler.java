import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// Handles importing and exporting Journey records to/from CSV files,
// and saving/loading today's journey session as JSON.

public class JourneyFileHandler {

    private static final String CSV_HEADER =
            "id,date,time,fromZone,toZone,timeBand,passengerType,zonesCrossed,baseFare,discountApplied,chargedFare";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");
    private static final String IMPORT_DIR = "imports/";
    private static final String EXPORT_DIR = "exports/";
    private static final String SESSION_DIR = "sessions/";


    // --- CSV export ---

    public boolean exportJourneysToCsv(List<Journey> journeys, String filename) {
        try {
            Files.createDirectories(Paths.get(EXPORT_DIR));
            String filepath = EXPORT_DIR + filename + ".csv";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
                writer.write(CSV_HEADER);
                writer.newLine();
                for (Journey journey : journeys) {
                    writer.write(journeyToCsvLine(journey));
                    writer.newLine();
                }
            }
            System.out.println("Journeys exported to: " + filepath);
            return true;
        } catch (IOException e) {
            System.out.println("Error exporting journeys: " + e.getMessage());
            return false;
        }
    }


    // --- CSV import ---

    public List<Journey> importJourneysFromCsv(String filename) {
        List<Journey> imported = new ArrayList<>();
        String filepath = IMPORT_DIR + filename + ".csv";

        if (!Files.exists(Paths.get(filepath))) {
            System.out.println("Import file not found: " + filepath);
            return imported;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("id")) continue;
                if (line.trim().isEmpty()) continue;

                Journey journey = parseCsvLine(line, lineNumber);
                if (journey != null) imported.add(journey);
            }
        } catch (IOException e) {
            System.out.println("Error importing journeys: " + e.getMessage());
        }

        return imported;
    }


    // --- JSON session save/load ---

    // Saves the current session's journeys as a JSON file so they can be
    // restored at the next launch.

    public boolean saveSessionJson(List<Journey> journeys, String riderName) {
        try {
            Files.createDirectories(Paths.get(SESSION_DIR));
            String filepath = SESSION_DIR + sanitise(riderName) + "_session.json";

            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < journeys.size(); i++) {
                sb.append(journeyToJson(journeys.get(i)));
                if (i < journeys.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");

            Files.writeString(Paths.get(filepath), sb.toString());
            System.out.println("Session saved to: " + filepath);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving session: " + e.getMessage());
            return false;
        }
    }


    // Loads a previously saved session for the given rider name.
    // Returns an empty list if no session file exists.

    public List<Journey> loadSessionJson(String riderName) {
        List<Journey> result = new ArrayList<>();
        String filepath = SESSION_DIR + sanitise(riderName) + "_session.json";

        if (!Files.exists(Paths.get(filepath))) return result;

        try {
            String json = Files.readString(Paths.get(filepath));
            result = parseJsonArray(json);
            System.out.println("Previous session loaded: " + result.size() + " journey(s) restored.");
        } catch (IOException e) {
            System.out.println("Error loading session: " + e.getMessage());
        }
        return result;
    }


    // Deletes a saved session file (called after a day reset).

    public void clearSessionJson(String riderName) {
        try {
            Files.deleteIfExists(Paths.get(SESSION_DIR + sanitise(riderName) + "_session.json"));
        } catch (IOException ignored) {}
    }


    // --- Private CSV helpers ---

    private String journeyToCsvLine(Journey journey) {
        String timeStr = (journey.getTime() != null)
                ? journey.getTime().format(TIME_FORMATTER) : "";
        return String.join(",",
                String.valueOf(journey.getJourneyId()),
                journey.getDate().format(DATE_FORMATTER),
                timeStr,
                String.valueOf(journey.getFromZone()),
                String.valueOf(journey.getToZone()),
                journey.getTimeBand().name(),
                journey.getPassengerType().name(),
                String.valueOf(journey.getZonesCrossed()),
                journey.getBaseFare().toPlainString(),
                journey.getDiscountApplied().toPlainString(),
                journey.getChargedFare().toPlainString()
        );
    }

    private Journey parseCsvLine(String line, int lineNumber) {
        String[] parts = line.split(",", -1);

        // Support both old format (10 fields, no time) and new format (11 fields)
        if (parts.length < 10) {
            System.out.printf("Warning: Line %d skipped (expected 10+ fields, got %d)%n",
                    lineNumber, parts.length);
            return null;
        }

        try {
            int col = 0;
            int id           = Integer.parseInt(parts[col++].trim());
            LocalDate date   = LocalDate.parse(parts[col++].trim(), DATE_FORMATTER);

            LocalTime time = null;
            // If the next field looks like a time (HH:MM), parse it; else treat as zone
            String nextField = parts[col].trim();
            if (nextField.matches("\\d{1,2}:\\d{2}")) {
                time = LocalTime.parse(nextField, TIME_FORMATTER);
                col++;
            }

            int fromZone     = Integer.parseInt(parts[col++].trim());
            int toZone       = Integer.parseInt(parts[col++].trim());
            CityRideDataset.TimeBand timeBand =
                    CityRideDataset.TimeBand.valueOf(parts[col++].trim());
            CityRideDataset.PassengerType passengerType =
                    CityRideDataset.PassengerType.valueOf(parts[col++].trim());
            int zonesCrossed = Integer.parseInt(parts[col++].trim());
            BigDecimal baseFare       = new BigDecimal(parts[col++].trim());
            BigDecimal discountApplied = new BigDecimal(parts[col++].trim());
            BigDecimal chargedFare    = new BigDecimal(parts[col].trim());

            Journey journey = new Journey(id, date, time, timeBand, fromZone, toZone, passengerType);
            journey.setZonesCrossed(zonesCrossed);
            journey.setBaseFare(baseFare);
            journey.setDiscountApplied(discountApplied);
            journey.setChargedFare(chargedFare);
            return journey;

        } catch (Exception e) {
            System.out.printf("Warning: Line %d skipped (%s)%n", lineNumber, e.getMessage());
            return null;
        }
    }


    // --- Private JSON helpers ---

    private String journeyToJson(Journey journey) {
        String timeStr = (journey.getTime() != null)
                ? journey.getTime().format(TIME_FORMATTER) : "";
        return "  {" +
                "\"id\":" + journey.getJourneyId() + "," +
                "\"date\":\"" + journey.getDate().format(DATE_FORMATTER) + "\"," +
                "\"time\":\"" + timeStr + "\"," +
                "\"fromZone\":" + journey.getFromZone() + "," +
                "\"toZone\":" + journey.getToZone() + "," +
                "\"timeBand\":\"" + journey.getTimeBand().name() + "\"," +
                "\"passengerType\":\"" + journey.getPassengerType().name() + "\"," +
                "\"zonesCrossed\":" + journey.getZonesCrossed() + "," +
                "\"baseFare\":" + journey.getBaseFare().toPlainString() + "," +
                "\"discountApplied\":" + journey.getDiscountApplied().toPlainString() + "," +
                "\"chargedFare\":" + journey.getChargedFare().toPlainString() +
                "}";
    }

    private List<Journey> parseJsonArray(String json) {
        List<Journey> result = new ArrayList<>();
        int i = 0;
        while (i < json.length()) {
            int open = json.indexOf('{', i);
            if (open == -1) break;
            int close = json.indexOf('}', open);
            if (close == -1) break;
            String obj = json.substring(open + 1, close);
            Journey j = parseJsonObject(obj);
            if (j != null) result.add(j);
            i = close + 1;
        }
        return result;
    }

    private Journey parseJsonObject(String obj) {
        try {
            int id             = Integer.parseInt(jsonString(obj, "id"));
            LocalDate date     = LocalDate.parse(jsonString(obj, "date"), DATE_FORMATTER);
            String timeStr     = jsonString(obj, "time");
            LocalTime time     = (timeStr != null && !timeStr.isEmpty())
                    ? LocalTime.parse(timeStr, TIME_FORMATTER) : null;
            int fromZone       = Integer.parseInt(jsonString(obj, "fromZone"));
            int toZone         = Integer.parseInt(jsonString(obj, "toZone"));
            CityRideDataset.TimeBand band =
                    CityRideDataset.TimeBand.valueOf(jsonString(obj, "timeBand"));
            CityRideDataset.PassengerType type =
                    CityRideDataset.PassengerType.valueOf(jsonString(obj, "passengerType"));
            int zonesCrossed   = Integer.parseInt(jsonString(obj, "zonesCrossed"));
            BigDecimal base    = new BigDecimal(jsonString(obj, "baseFare"));
            BigDecimal disc    = new BigDecimal(jsonString(obj, "discountApplied"));
            BigDecimal charged = new BigDecimal(jsonString(obj, "chargedFare"));

            Journey j = new Journey(id, date, time, band, fromZone, toZone, type);
            j.setZonesCrossed(zonesCrossed);
            j.setBaseFare(base);
            j.setDiscountApplied(disc);
            j.setChargedFare(charged);
            return j;
        } catch (Exception e) {
            System.out.println("Warning: Skipped a session record (" + e.getMessage() + ")");
            return null;
        }
    }

    // Extracts a value from a JSON object string by key.
    // Works for both quoted string values and bare numbers.
    private String jsonString(String obj, String key) {
        String search = "\"" + key + "\":";
        int idx = obj.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        while (start < obj.length() && obj.charAt(start) == ' ') start++;
        if (start >= obj.length()) return "";
        if (obj.charAt(start) == '"') {
            int end = obj.indexOf('"', start + 1);
            return end == -1 ? "" : obj.substring(start + 1, end);
        } else {
            int end = start;
            while (end < obj.length() && obj.charAt(end) != ',' && obj.charAt(end) != '}') end++;
            return obj.substring(start, end).trim();
        }
    }

    private String sanitise(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase();
    }
}
