import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class ReportExporter {

    private static final String REPORT_DIR = "reports/";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Exports a CSV report containing every journey and a summary footer.

    public boolean exportCsvReport(JourneyManager journeyManager, String riderName, LocalDate date) {
        try {
            Files.createDirectories(Paths.get(REPORT_DIR));
            String filename = buildFilename(riderName, date, "csv");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                // Header
                writer.write("id,date,time,fromZone,toZone,timeBand,passengerType," +
                        "zonesCrossed,baseFare,discountApplied,chargedFare");
                writer.newLine();

                // Journey rows
                for (Journey journey : journeyManager.getAllJourneys()) {
                    writer.write(journeyToCsvLine(journey));
                    writer.newLine();
                }

                // Summary section
                writer.newLine();
                writer.write("--- SUMMARY ---");
                writer.newLine();
                writer.write("Total journeys," + journeyManager.getJourneyCount());
                writer.newLine();
                writer.write("Total cost,£" + journeyManager.getTotalCost().toPlainString());
                writer.newLine();
                writer.write("Average cost per journey,£" +
                        journeyManager.getAverageCostPerJourney().toPlainString());
                writer.newLine();

                Journey mostExpensive = journeyManager.getMostExpensiveJourney();
                if (mostExpensive != null) {
                    writer.write("Most expensive journey,ID " + mostExpensive.getJourneyId() +
                            ",£" + mostExpensive.getChargedFare().toPlainString());
                    writer.newLine();
                }

                // Cap status per passenger type
                for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
                    boolean capReached = journeyManager.isCapReached(type);
                    writer.write("Cap reached (" + type + ")," + (capReached ? "YES" : "NO"));
                    writer.newLine();
                }
            }

            System.out.println("CSV report saved to: " + filename);
            return true;
        } catch (IOException e) {
            System.out.println("Error exporting CSV report: " + e.getMessage());
            return false;
        }
    }


    //Exports a human-readable text summary of the day's journeys.

    public boolean exportTextSummary(JourneyManager journeyManager, String riderName, LocalDate date) {
        try {
            Files.createDirectories(Paths.get(REPORT_DIR));
            String filename = buildFilename(riderName, date, "txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writeSeparator(writer, "=", 60);
                writer.write("  CITYRIDE LITE - END OF DAY SUMMARY");
                writer.newLine();
                writer.write("  Rider: " + riderName);
                writer.newLine();
                writer.write("  Date:  " + date.format(DATE_FORMATTER));
                writer.newLine();
                writeSeparator(writer, "=", 60);
                writer.newLine();

                List<Journey> journeys = journeyManager.getAllJourneys();

                if (journeys.isEmpty()) {
                    writer.write("No journeys recorded today.");
                    writer.newLine();
                } else {
                    // Journey list
                    writer.write("JOURNEYS:");
                    writer.newLine();
                    writeSeparator(writer, "-", 60);
                    for (Journey journey : journeys) {
                        writer.write(journey.toString());
                        writer.newLine();
                    }
                    writeSeparator(writer, "-", 60);
                    writer.newLine();

                    // Summary statistics
                    writer.write("DAILY TOTALS:");
                    writer.newLine();
                    writer.write(String.format("  Total journeys       : %d%n",
                            journeyManager.getJourneyCount()));
                    writer.write(String.format("  Total cost charged   : £%.2f%n",
                            journeyManager.getTotalCost()));
                    writer.write(String.format("  Average cost/journey : £%.2f%n",
                            journeyManager.getAverageCostPerJourney()));

                    Journey mostExpensive = journeyManager.getMostExpensiveJourney();
                    if (mostExpensive != null) {
                        writer.write(String.format("  Most expensive       : Journey #%d (£%.2f)%n",
                                mostExpensive.getJourneyId(), mostExpensive.getChargedFare()));
                    }
                    writer.newLine();

                    // Savings section
                    writer.write("SAVINGS BREAKDOWN:");
                    writer.newLine();
                    for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
                        int count = journeyManager.getJourneyCountByType(type);
                        if (count == 0) {
                            continue;
                        }
                        BigDecimal preDiscount = journeyManager.getPreDiscountTotalByType(type);
                        BigDecimal charged = journeyManager.getPassengerTotal(type);
                        BigDecimal savings = preDiscount.subtract(charged);
                        boolean capReached = journeyManager.isCapReached(type);

                        writer.write(String.format("  %-15s : %d journey(s) | " +
                                        "Undiscounted: £%.2f | Charged: £%.2f | Saved: £%.2f | Cap: %s%n",
                                type, count, preDiscount, charged, savings,
                                capReached ? "REACHED" : "Not reached"));
                    }
                    writer.newLine();

                    // Category counts
                    writer.write("JOURNEY CATEGORIES:");
                    writer.newLine();
                    Map<String, Integer> timeBandCounts = journeyManager.getTimeBandCounts();
                    writer.write(String.format("  Peak journeys     : %d%n", timeBandCounts.get("PEAK")));
                    writer.write(String.format("  Off-peak journeys : %d%n", timeBandCounts.get("OFF_PEAK")));

                    writer.newLine();
                    writer.write("ZONE USAGE:");
                    writer.newLine();
                    Map<String, Integer> zoneUsage = journeyManager.getZonePairCounts();
                    for (Map.Entry<String, Integer> entry : zoneUsage.entrySet()) {
                        writer.write(String.format("  %-15s : %d time(s)%n",
                                entry.getKey(), entry.getValue()));
                    }
                }

                writer.newLine();
                writeSeparator(writer, "=", 60);
            }

            System.out.println("Text summary saved to: " + filename);
            return true;
        } catch (IOException e) {
            System.out.println("Error exporting text summary: " + e.getMessage());
            return false;
        }
    }


    // Private helpers
    private String buildFilename(String riderName, LocalDate date, String extension) {
        String safeName = riderName.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase();
        String dateStr = date.format(FILE_DATE_FORMATTER);
        return REPORT_DIR + safeName + "_" + dateStr + "." + extension;
    }

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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

    private void writeSeparator(BufferedWriter writer, String char_, int length) throws IOException {
        writer.write(char_.repeat(length));
        writer.newLine();
    }
}
