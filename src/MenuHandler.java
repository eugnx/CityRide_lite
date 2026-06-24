import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class MenuHandler {

    // Main menu constants
    private static final int MENU_PROFILE         = 1;
    private static final int MENU_ADD_JOURNEY     = 2;
    private static final int MENU_LIST_JOURNEYS   = 3;
    private static final int MENU_FILTER_JOURNEYS = 4;
    private static final int MENU_EDIT_JOURNEY    = 5;
    private static final int MENU_REMOVE_JOURNEY  = 6;
    private static final int MENU_DAILY_SUMMARY   = 7;
    private static final int MENU_TOTALS_BY_TYPE  = 8;
    private static final int MENU_IMPORT_CSV      = 9;
    private static final int MENU_EXPORT_REPORTS  = 10;
    private static final int MENU_RESET_DAY       = 11;
    private static final int MENU_EXIT            = 12;

    // Filter sub-menu constants
    private static final int FILTER_BY_PASSENGER_TYPE = 1;
    private static final int FILTER_BY_TIME_BAND      = 2;
    private static final int FILTER_BY_ZONE           = 3;
    private static final int FILTER_BY_DATE           = 4;
    private static final int FILTER_BACK_TO_MAIN      = 5;

    private final Scanner scanner;
    private final JourneyManager journeyManager;
    private final JourneyFileHandler journeyFileHandler;
    private final ReportExporter reportExporter;
    private final ProfileFileHandler profileFileHandler;
    private RiderProfile riderProfile;
    private boolean running;

    public MenuHandler(Scanner scanner) {
        this.scanner            = scanner;
        this.journeyManager     = new JourneyManager();
        this.journeyFileHandler = new JourneyFileHandler();
        this.reportExporter     = new ReportExporter();
        this.profileFileHandler = new ProfileFileHandler();
        this.riderProfile       = null;
        this.running            = true;
    }


    // Starts the Rider menu loop.

    public void start() {
        displayWelcomeMessage();

        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            processMenuChoice(choice);
        }

        offerSaveOnExit();
        System.out.println("\nThank you for using CityRide Lite!");
    }


    // Menu display and dispatch

    private void displayWelcomeMessage() {
        System.out.println("\nProfile loaded: " +
                (riderProfile != null ? riderProfile.getName() : "None (use option 1 to set up)"));
    }

    private void displayMainMenu() {
        String profileStatus = riderProfile != null ? riderProfile.getName() : "No profile";
        System.out.println("\n========== MAIN MENU (" + profileStatus + ") ==========");
        System.out.println(" 1. Manage Profile");
        System.out.println(" 2. Add Journey");
        System.out.println(" 3. List All Journeys");
        System.out.println(" 4. Filter Journeys");
        System.out.println(" 5. Edit Journey");
        System.out.println(" 6. Remove Journey");
        System.out.println(" 7. View Daily Summary");
        System.out.println(" 8. View Totals by Passenger Type");
        System.out.println(" 9. Import Journeys from CSV");
        System.out.println("10. Export Reports");
        System.out.println("11. Reset Day");
        System.out.println("12. Exit");
        System.out.println("=".repeat(43));
    }

    private void processMenuChoice(int choice) {
        switch (choice) {
            case MENU_PROFILE:         manageProfile();          break;
            case MENU_ADD_JOURNEY:     addJourney();             break;
            case MENU_LIST_JOURNEYS:   listAllJourneys();        break;
            case MENU_FILTER_JOURNEYS: filterJourneys();         break;
            case MENU_EDIT_JOURNEY:    editJourney();            break;
            case MENU_REMOVE_JOURNEY:  removeJourney();          break;
            case MENU_DAILY_SUMMARY:   displayDailySummary();    break;
            case MENU_TOTALS_BY_TYPE:  displayTotalsByType();    break;
            case MENU_IMPORT_CSV:      importJourneysFromCsv();  break;
            case MENU_EXPORT_REPORTS:  exportReports();          break;
            case MENU_RESET_DAY:       resetDay();               break;
            case MENU_EXIT:            running = false;          break;
            default:
                System.out.println("Invalid choice. Please enter 1-12.");
        }
    }


    // Profile management

    private void manageProfile() {
        System.out.println("\n--- Manage Profile ---");
        System.out.println("1. Create new profile");
        System.out.println("2. Load existing profile");
        System.out.println("3. View current profile");
        System.out.println("4. Back to main menu");

        int choice = getIntInput("Enter choice (1-4): ");
        switch (choice) {
            case 1: createProfile();  break;
            case 2: loadProfile();    break;
            case 3: viewProfile();    break;
            case 4: break;
            default: System.out.println("Invalid option.");
        }
    }

    private void createProfile() {
        System.out.println("\n--- Create New Profile ---");

        System.out.print("Enter your name (e.g. Alice Smith): ");
        String name = scanner.nextLine().trim();
        if (!InputValidator.isNonBlank(name)) {
            System.out.println("Error: Name cannot be blank.");
            return;
        }

        CityRideDataset.PassengerType type = getPassengerTypeInput();
        PaymentOption payment = getPaymentOptionInput();

        riderProfile = new RiderProfile(name, type, payment);
        System.out.println("\nProfile created:");
        System.out.println("  " + riderProfile);

        // Offer to restore a previous session for this rider
        offerSessionRestore(name);

        System.out.print("Save this profile to file? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean saved = profileFileHandler.saveProfile(riderProfile);
            if (saved) System.out.println("Profile saved successfully.");
        }
    }

    private void loadProfile() {
        System.out.println("\n--- Load Profile ---");
        String[] saved = profileFileHandler.listSavedProfiles();

        if (saved.length == 0) {
            System.out.println("No saved profiles found. Create one first (option 1).");
            return;
        }

        System.out.println("Saved profiles:");
        for (int i = 0; i < saved.length; i++) {
            System.out.println("  " + (i + 1) + ". " + saved[i]);
        }

        System.out.print("Enter rider name to load (e.g. Alice Smith): ");
        String name = scanner.nextLine().trim();

        RiderProfile loaded = profileFileHandler.loadProfile(name);
        if (loaded != null) {
            riderProfile = loaded;
            System.out.println("Profile loaded: " + riderProfile);
            offerSessionRestore(name);
        }
    }

    private void viewProfile() {
        if (riderProfile == null) {
            System.out.println("No profile active. Use option 1 to create or load one.");
        } else {
            System.out.println("\n--- Current Profile ---");
            System.out.println(riderProfile);
        }
    }

    // Offers to restore journeys from a saved session for this rider.
    private void offerSessionRestore(String riderName) {
        List<Journey> session = journeyFileHandler.loadSessionJson(riderName);
        if (!session.isEmpty()) {
            System.out.print("Restore " + session.size() + " journey(s) from your last session? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                for (Journey j : session) {
                    journeyManager.addJourney(j);
                }
                System.out.println(session.size() + " journey(s) restored.");
            }
        }
    }


    // Journey operations

    private void addJourney() {
        System.out.println("\n--- Add New Journey ---");

        LocalDate date = getDateInput("Enter date (DD/MM/YYYY) or press Enter for today (e.g. 24/06/2026): ");
        LocalTime time = getTimeInput("Enter time (HH:MM) or press Enter for now (e.g. 08:30): ");
        CityRideDataset.TimeBand timeBand = getTimeBandInput();
        int fromZone = getZoneInput("Enter starting zone (1-5, e.g. 1): ");
        int toZone   = getZoneInput("Enter destination zone (1-5, e.g. 3): ");

        CityRideDataset.PassengerType passengerType;
        if (riderProfile != null) {
            System.out.println("Passenger type from profile: " + riderProfile.getPassengerType());
            System.out.print("Use profile type? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                passengerType = riderProfile.getPassengerType();
            } else {
                passengerType = getPassengerTypeInput();
            }
        } else {
            passengerType = getPassengerTypeInput();
        }

        Journey journey = new Journey(0, date, time, timeBand, fromZone, toZone, passengerType);
        Journey completed = journeyManager.addJourney(journey);

        System.out.println("\nJourney added successfully!");
        System.out.println("  Journey ID    : " + completed.getJourneyId());
        System.out.printf("  Zones crossed : %d%n", completed.getZonesCrossed());
        System.out.printf("  Base fare     : £%.2f%n", completed.getBaseFare());
        System.out.printf("  Charged fare  : £%.2f%n", completed.getChargedFare());
        System.out.printf("  Running total : £%.2f%n",
                journeyManager.getPassengerTotal(passengerType));

        if (journeyManager.isCapReached(passengerType)) {
            System.out.println("  Daily cap reached for " + passengerType + "!");
        }
    }


    // Allows the rider to edit the from/to zones and time band of an existing journey.
    // Recalculates all fares after the edit.

    private void editJourney() {
        System.out.println("\n--- Edit Journey ---");

        if (journeyManager.getJourneyCount() == 0) {
            System.out.println("No journeys to edit.");
            return;
        }

        listAllJourneys();
        int id = getIntInput("Enter journey ID to edit (e.g. 1): ");
        Journey journey = journeyManager.getJourneyById(id);

        if (journey == null) {
            System.out.println("Error: Journey ID " + id + " not found.");
            return;
        }

        System.out.println("Editing: " + journey);
        System.out.println("Press Enter to keep the current value.");

        // From zone
        System.out.print("New starting zone (1-5) [current: " + journey.getFromZone() + "]: ");
        String fromInput = scanner.nextLine().trim();
        if (!fromInput.isEmpty()) {
            if (InputValidator.isValidInteger(fromInput)) {
                int newFrom = Integer.parseInt(fromInput);
                if (InputValidator.isValidZone(newFrom)) {
                    journey.setFromZone(newFrom);
                } else {
                    System.out.println("Invalid zone; keeping original.");
                }
            } else {
                System.out.println("Invalid input; keeping original.");
            }
        }

        // To zone
        System.out.print("New destination zone (1-5) [current: " + journey.getToZone() + "]: ");
        String toInput = scanner.nextLine().trim();
        if (!toInput.isEmpty()) {
            if (InputValidator.isValidInteger(toInput)) {
                int newTo = Integer.parseInt(toInput);
                if (InputValidator.isValidZone(newTo)) {
                    journey.setToZone(newTo);
                } else {
                    System.out.println("Invalid zone; keeping original.");
                }
            } else {
                System.out.println("Invalid input; keeping original.");
            }
        }

        // Time band
        System.out.print("New time band (PEAK/OFF_PEAK) [current: " + journey.getTimeBand() + "]: ");
        String bandInput = scanner.nextLine().trim();
        if (!bandInput.isEmpty()) {
            if (InputValidator.isValidTimeBand(bandInput)) {
                journey.setTimeBand(InputValidator.parseTimeBand(bandInput));
            } else {
                System.out.println("Invalid time band; keeping original.");
            }
        }

        journeyManager.recalculateAllFares();
        System.out.println("Journey updated. All totals recalculated.");
        System.out.println("Updated: " + journeyManager.getJourneyById(id));
    }

    private void listAllJourneys() {
        List<Journey> journeys = journeyManager.getAllJourneys();
        System.out.println("\n--- All Journeys ---");

        if (journeys.isEmpty()) {
            System.out.println("No journeys recorded yet.");
            return;
        }

        System.out.println("Total journeys: " + journeys.size());
        System.out.println("-".repeat(90));
        for (Journey journey : journeys) {
            System.out.println(journey);
        }
        System.out.println("-".repeat(90));
        System.out.printf("Total cost for the day: £%.2f%n", journeyManager.getTotalCost());
    }

    private void removeJourney() {
        System.out.println("\n--- Remove Journey ---");

        if (journeyManager.getJourneyCount() == 0) {
            System.out.println("No journeys to remove.");
            return;
        }

        listAllJourneys();
        int id = getIntInput("Enter journey ID to remove (e.g. 1): ");
        Journey toRemove = journeyManager.getJourneyById(id);

        if (toRemove == null) {
            System.out.println("Error: Journey ID " + id + " not found.");
            return;
        }

        System.out.print("Remove journey " + id + "? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean removed = journeyManager.removeJourneyById(id);
            if (removed) {
                System.out.println("Journey " + id + " removed. All totals recalculated.");
            } else {
                System.out.println("Error: Could not remove journey.");
            }
        } else {
            System.out.println("Removal cancelled.");
        }
    }


    // Filter journeys

    private void filterJourneys() {
        boolean filterActive = true;
        while (filterActive) {
            displayFilterMenu();
            int choice = getIntInput("Enter your choice (1-5): ");
            switch (choice) {
                case FILTER_BY_PASSENGER_TYPE: filterByPassengerType(); break;
                case FILTER_BY_TIME_BAND:      filterByTimeBand();      break;
                case FILTER_BY_ZONE:           filterByZone();          break;
                case FILTER_BY_DATE:           filterByDate();          break;
                case FILTER_BACK_TO_MAIN:      filterActive = false;    break;
                default: System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }

    private void displayFilterMenu() {
        System.out.println("\n========== FILTER MENU ==========");
        System.out.println("1. Filter by Passenger Type");
        System.out.println("2. Filter by Time Band");
        System.out.println("3. Filter by Zone");
        System.out.println("4. Filter by Date");
        System.out.println("5. Back to Main Menu");
        System.out.println("=================================");
    }

    private void filterByPassengerType() {
        CityRideDataset.PassengerType type = getPassengerTypeInput();
        displayFilterResults(journeyManager.getJourneysByPassengerType(type),
                "Passenger Type: " + type);
    }

    private void filterByTimeBand() {
        CityRideDataset.TimeBand band = getTimeBandInput();
        displayFilterResults(journeyManager.getJourneysByTimeBand(band),
                "Time Band: " + band);
    }

    private void filterByZone() {
        System.out.println("\n--- Filter by Zone ---");
        int zone = getZoneInput("Enter zone number (1-5, e.g. 2): ");
        System.out.println("1. Starting zone (from)");
        System.out.println("2. Destination zone (to)");
        System.out.println("3. Either zone");
        int choice = getIntInput("Enter choice (1-3): ");

        List<Journey> filtered;
        switch (choice) {
            case 1:  filtered = journeyManager.getJourneysByFromZone(zone); break;
            case 2:  filtered = journeyManager.getJourneysByToZone(zone);   break;
            case 3:  filtered = journeyManager.getJourneysByZone(zone);     break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        displayFilterResults(filtered, "Zone: " + zone);
    }

    private void filterByDate() {
        System.out.println("\n--- Filter by Date ---");
        LocalDate date = getDateInput("Enter date (DD/MM/YYYY, e.g. 24/06/2026): ");
        displayFilterResults(journeyManager.getJourneysByDate(date), "Date: " + date);
    }

    private void displayFilterResults(List<Journey> journeys, String filterDesc) {
        System.out.println("\n--- Filter Results: " + filterDesc + " ---");
        if (journeys.isEmpty()) {
            System.out.println("No journeys match this filter.");
            return;
        }
        System.out.println("Matching journeys: " + journeys.size());
        System.out.println("-".repeat(90));
        for (Journey journey : journeys) {
            System.out.println(journey);
        }
        System.out.println("-".repeat(90));
        BigDecimal total = BigDecimal.ZERO;
        for (Journey journey : journeys) {
            total = total.add(journey.getChargedFare());
        }
        System.out.printf("Total cost for filtered journeys: £%.2f%n", total);
    }


    // Summary and totals display

    private void displayDailySummary() {
        System.out.println("\n========== DAILY SUMMARY ==========");

        if (journeyManager.getJourneyCount() == 0) {
            System.out.println("No journeys recorded today.");
            return;
        }

        BigDecimal totalCost    = journeyManager.getTotalCost();
        BigDecimal averageCost  = journeyManager.getAverageCostPerJourney();
        Journey mostExpensive   = journeyManager.getMostExpensiveJourney();

        System.out.printf("Total journeys       : %d%n", journeyManager.getJourneyCount());
        System.out.printf("Total cost charged   : £%.2f%n", totalCost);
        System.out.printf("Average cost/journey : £%.2f%n", averageCost);

        if (mostExpensive != null) {
            System.out.printf("Most expensive       : Journey #%d (£%.2f)%n",
                    mostExpensive.getJourneyId(), mostExpensive.getChargedFare());
        }

        System.out.println("\n--- Cap Status & Savings ---");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            int count = journeyManager.getJourneyCountByType(type);
            if (count == 0) continue;
            BigDecimal preDiscount = journeyManager.getPreDiscountTotalByType(type);
            BigDecimal charged     = journeyManager.getPassengerTotal(type);
            BigDecimal savings     = preDiscount.subtract(charged);
            boolean capReached     = journeyManager.isCapReached(type);

            System.out.printf("  %-15s %d journey(s) | Saved: £%.2f | Cap: %s%n",
                    type, count, savings, capReached ? "REACHED" : "Not reached");
        }

        displayCategoryCounts();
    }

    private void displayCategoryCounts() {
        Map<String, Integer> timeBandCounts = journeyManager.getTimeBandCounts();
        Map<String, Integer> zonePairCounts = journeyManager.getZonePairCounts();

        System.out.println("\n--- Category Counts ---");
        System.out.println("Peak journeys     : " + timeBandCounts.get("PEAK"));
        System.out.println("Off-peak journeys : " + timeBandCounts.get("OFF_PEAK"));

        System.out.println("\nZone pair usage:");
        if (zonePairCounts.isEmpty()) {
            System.out.println("  No zone data available.");
        } else {
            for (Map.Entry<String, Integer> entry : zonePairCounts.entrySet()) {
                System.out.printf("  %-20s : %d trip(s)%n", entry.getKey(), entry.getValue());
            }
        }
    }

    private void displayTotalsByType() {
        System.out.println("\n========== TOTALS BY PASSENGER TYPE ==========");
        System.out.printf("%-15s %10s %12s %12s %10s %12s%n",
                "Type", "Journeys", "Pre-Discount", "Discounted", "Charged", "Cap Reached");
        System.out.println("-".repeat(75));

        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            int journeyCount       = journeyManager.getJourneyCountByType(type);
            BigDecimal preDiscount = journeyManager.getPreDiscountTotalByType(type);
            BigDecimal discounted  = journeyManager.getDiscountedTotalByType(type);
            BigDecimal charged     = journeyManager.getPassengerTotal(type);
            boolean capReached     = journeyManager.isCapReached(type);

            System.out.printf("%-15s %10d £%10.2f £%10.2f £%8.2f %12s%n",
                    type, journeyCount, preDiscount, discounted, charged,
                    capReached ? "YES" : "NO");
        }
        System.out.println("=".repeat(75));
    }


    // CSV import / export

    private void importJourneysFromCsv() {
        System.out.println("\n--- Import Journeys from CSV ---");
        System.out.println("Place your CSV file in the 'imports/' folder.");
        System.out.print("Enter filename without .csv extension (e.g. journeys_today): ");
        String filename = scanner.nextLine().trim();

        if (!InputValidator.isNonBlank(filename)) {
            System.out.println("Error: Filename cannot be blank.");
            return;
        }

        List<Journey> imported = journeyFileHandler.importJourneysFromCsv(filename);

        if (imported.isEmpty()) {
            System.out.println("No journeys were imported.");
            return;
        }

        System.out.println(imported.size() + " journey(s) found. Adding to today's session...");
        for (Journey journey : imported) {
            journeyManager.addJourney(journey);
        }
        System.out.println(imported.size() + " journey(s) imported successfully.");
    }

    private void exportReports() {
        System.out.println("\n--- Export Reports ---");

        if (journeyManager.getJourneyCount() == 0) {
            System.out.println("No journeys to export.");
            return;
        }

        String riderName = riderProfile != null ? riderProfile.getName() : "unknown_rider";
        LocalDate today  = LocalDate.now();

        System.out.println("1. Export CSV journey report");
        System.out.println("2. Export text summary");
        System.out.println("3. Export both");
        System.out.println("4. Export journeys CSV (raw data only)");
        int choice = getIntInput("Enter choice (1-4): ");

        switch (choice) {
            case 1:
                reportExporter.exportCsvReport(journeyManager, riderName, today);
                break;
            case 2:
                reportExporter.exportTextSummary(journeyManager, riderName, today);
                break;
            case 3:
                reportExporter.exportCsvReport(journeyManager, riderName, today);
                reportExporter.exportTextSummary(journeyManager, riderName, today);
                break;
            case 4:
                String filename = riderName + "_journeys_" + today;
                journeyFileHandler.exportJourneysToCsv(journeyManager.getAllJourneys(), filename);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }


    // Reset and exit

    private void resetDay() {
        System.out.println("\n--- Reset Day ---");
        System.out.print("WARNING: This will delete ALL journeys for today. Continue? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            journeyManager.resetDay();
            if (riderProfile != null) {
                journeyFileHandler.clearSessionJson(riderProfile.getName());
            }
            System.out.println("Day reset. All journeys cleared.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }


    // When the user exits, offer to save profile and persist journeys as JSON.

    private void offerSaveOnExit() {
        System.out.println("\n--- Save Before Exit ---");

        if (riderProfile != null && journeyManager.getJourneyCount() > 0) {
            System.out.print("Save current journeys for next session? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                journeyFileHandler.saveSessionJson(journeyManager.getAllJourneys(),
                        riderProfile.getName());
            }

            System.out.print("Export journey report? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                reportExporter.exportCsvReport(journeyManager, riderProfile.getName(), LocalDate.now());
                reportExporter.exportTextSummary(journeyManager, riderProfile.getName(), LocalDate.now());
            }
        }

        if (riderProfile != null) {
            System.out.print("Save profile? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                profileFileHandler.saveProfile(riderProfile);
            }
        }
    }


    // Input helpers

    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return LocalDate.now();
            if (InputValidator.isValidDate(input)) return InputValidator.parseDate(input);
            System.out.println("Error: Invalid date format. Use DD/MM/YYYY (e.g. 24/06/2026).");
        }
    }

    private LocalTime getTimeInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return LocalTime.now().withSecond(0).withNano(0);
            if (InputValidator.isValidTime(input)) return InputValidator.parseTime(input);
            System.out.println("Error: Invalid time format. Use HH:MM (e.g. 08:30).");
        }
    }

    private CityRideDataset.TimeBand getTimeBandInput() {
        while (true) {
            System.out.print("Enter time band (PEAK or OFF_PEAK, e.g. PEAK): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidTimeBand(input)) return InputValidator.parseTimeBand(input);
            System.out.println("Error: Invalid time band. Enter PEAK or OFF_PEAK.");
        }
    }

    private int getZoneInput(String prompt) {
        while (true) {
            int zone = getIntInput(prompt);
            if (InputValidator.isValidZone(zone)) return zone;
            System.out.println("Error: Zone must be between 1 and 5.");
        }
    }

    private CityRideDataset.PassengerType getPassengerTypeInput() {
        while (true) {
            System.out.print("Enter passenger type (ADULT/STUDENT/CHILD/SENIOR_CITIZEN): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidPassengerType(input)) return InputValidator.parsePassengerType(input);
            System.out.println("Error: Invalid passenger type. Choose from: ADULT, STUDENT, CHILD, SENIOR_CITIZEN.");
        }
    }

    private PaymentOption getPaymentOptionInput() {
        System.out.println("Payment options:");
        PaymentOption[] options = PaymentOption.values();
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i].getDisplayLabel());
        }

        while (true) {
            System.out.print("Enter choice (1-" + options.length + "): ");
            String input = scanner.nextLine().trim();
            PaymentOption option = InputValidator.parsePaymentOption(input);
            if (option != null) return option;
            System.out.println("Error: Please enter a number between 1 and " + options.length + ".");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Error: Input cannot be blank.");
            } else if (InputValidator.isValidInteger(input)) {
                return Integer.parseInt(input);
            } else {
                System.out.println("Error: Please enter a valid whole number.");
            }
        }
    }
}
