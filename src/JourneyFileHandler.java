import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// Handles importing and exporting Journey records to/from CSV files.

public class JourneyFileHandler {

    private static final String CSV_HEADER =
            "id,date,fromZone,toZone,timeBand,passengerType,zonesCrossed,baseFare,discountApplied,chargedFare";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String IMPORT_DIR = "imports/";
    private static final String EXPORT_DIR = "exports/";


    // Exports a list of journeys to a CSV file in the exports/ directory.

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


    //Imports journeys from a CSV file in the imports/ directory.
    // Lines with invalid data are skipped with a warning; valid lines are returned.

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
                if (lineNumber == 1 && line.startsWith("id")) {
                    continue; // skip header
                }
                if (line.trim().isEmpty()) {
                    continue;
                }

                Journey journey = parseCsvLine(line, lineNumber);
                if (journey != null) {
                    imported.add(journey);
                }
            }
        } catch (IOException e) {
            System.out.println("Error importing journeys: " + e.getMessage());
        }

        return imported;
    }

    // Private helpers


    //Converts a Journey object into a CSV line string.

    private String journeyToCsvLine(Journey journey) {
        return String.join(",",
                String.valueOf(journey.getJourneyId()),
                journey.getDate().format(DATE_FORMATTER),
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


    //Parses a single CSV line into a Journey object.
    // Returns null and prints a warning if parsing fails.

    private Journey parseCsvLine(String line, int lineNumber) {
        String[] parts = line.split(",", -1);

        if (parts.length < 10) {
            System.out.printf("Warning: Line %d skipped (expected 10 fields, got %d)%n",
                    lineNumber, parts.length);
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            LocalDate date = LocalDate.parse(parts[1].trim(), DATE_FORMATTER);
            int fromZone = Integer.parseInt(parts[2].trim());
            int toZone = Integer.parseInt(parts[3].trim());
            CityRideDataset.TimeBand timeBand = CityRideDataset.TimeBand.valueOf(parts[4].trim());
            CityRideDataset.PassengerType passengerType = CityRideDataset.PassengerType.valueOf(parts[5].trim());
            int zonesCrossed = Integer.parseInt(parts[6].trim());
            BigDecimal baseFare = new BigDecimal(parts[7].trim());
            BigDecimal discountApplied = new BigDecimal(parts[8].trim());
            BigDecimal chargedFare = new BigDecimal(parts[9].trim());

            Journey journey = new Journey(id, date, timeBand, fromZone, toZone, passengerType);
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
}
