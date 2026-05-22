import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Map;



public class MenuHandler {

    private static final int MENU_ADD_JOURNEY = 1;
    private static final int MENU_LIST_JOURNEYS = 2;
    private static final int MENU_FILTER_JOURNEYS = 3;
    private static final int MENU_DAILY_SUMMARY = 4;
    private static final int MENU_TOTALS_BY_TYPE = 5;
    private static final int MENU_REMOVE_JOURNEY= 6;
    private static final int MENU_RESET_DAY = 7;
    private static final int MENU_EXIT = 8;

    // sub-menu constants
    private static final int FILTER_BY_PASSENGER_TYPE = 1;
    private static final int FILTER_BY_TIME_BAND = 2;
    private static final int FILTER_BY_ZONE = 3;
    private static final int FILTER_BY_DATE = 4;
    private static final int FILTER_BACK_TO_MAIN = 5;


// private fileds
    private Scanner scanner;
    private JourneyManager journeyManager;
    private boolean running;


    public MenuHandler() {
        this.scanner = new Scanner(System.in);
        this.journeyManager = new JourneyManager();
        this.running = true;
    }

    public void start() {
        displayWelcomeMessage();

        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            processMenuChoice(choice);
        }

        System.out.println("\nThank you for using CityRide Lite!");
        scanner.close(); //  used to close the Scanner object when you are finished using it
    }

    private void displayWelcomeMessage() {
        System.out.println("\n=========================================");
        System.out.println("     WELCOME TO CITYRIDE LITE");
        System.out.println("=========================================");
    }

    private void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Add Journey");
        System.out.println("2. List All Journeys");
        System.out.println("3. Filter Journeys");
        System.out.println("4. View Daily Summary");
        System.out.println("5. View Totals by Passenger Type");
        System.out.println("6. Remove Journey");
        System.out.println("7. Reset Day");
        System.out.println("8. Exit");
        System.out.println("================================");
    }

    private void processMenuChoice(int choice) {
        switch (choice) {
            case MENU_ADD_JOURNEY:
                addJourney();
                break;

            case MENU_LIST_JOURNEYS:
                listAllJourneys();
                break;

            case MENU_FILTER_JOURNEYS:
                filterJourneys();
                break;

            case MENU_DAILY_SUMMARY:
                displayDailySummary();
                break;

            case MENU_TOTALS_BY_TYPE:
                displayTotalsByType();
                break;

            case MENU_REMOVE_JOURNEY:
                removeJourney();
                break;

            case MENU_RESET_DAY:
                resetDay();
                break;

            case MENU_EXIT:
                running = false;
                break;

            default:
                System.out.println("Invalid choice! Please enter 1-8.");
        }
    }


    private void addJourney() {
        System.out.println("\n--- Add New Journey ---");

        LocalDate date = getDateInput("Enter date (DD/MM/YYYY) or press Enter for today: ");
        CityRideDataset.TimeBand timeBand = getTimeBandInput();
        int fromZone = getZoneInput("Enter starting zone (1-5): ");
        int toZone = getZoneInput("Enter destination zone (1-5): ");
        CityRideDataset.PassengerType passengerType = getPassengerTypeInput();

        Journey journey = new Journey(0, date, timeBand, fromZone, toZone, passengerType);
        Journey completedJourney = journeyManager.addJourney(journey);

        System.out.println("\n✓ Journey added successfully!");
        System.out.println("  Journey ID: " + completedJourney.getJourneyId());
        System.out.printf("  Zones crossed: %d%n", completedJourney.getZonesCrossed());
        System.out.printf("  Base fare: £%.2f%n", completedJourney.getBaseFare());
        System.out.printf("  Charged fare: £%.2f%n", completedJourney.getChargedFare());

        if (journeyManager.isCapReached(passengerType)) {
            System.out.println("  ⚠ Daily cap reached for " + passengerType + "!");
        }
    }

    private void listAllJourneys() {
        List<Journey> journeys = journeyManager.getAllJourneys();

        System.out.println("\n--- All Journeys ---");

        if (journeys.isEmpty()) {
            System.out.println("No journeys recorded yet.");
        } else {
            System.out.println("\nTotal journeys: " + journeys.size());
            System.out.println("-".repeat(80));

            for (Journey journey : journeys) {
                System.out.println(journey);
            }

            System.out.println("-".repeat(80));
            System.out.printf("Total cost for the day: £%.2f%n", journeyManager.getTotalCost());
        }
    }

    private void filterJourneys() {
        boolean filterMenuActive = true;

        while (filterMenuActive == true) {
            displayFilterMenu();
            int choice = getIntInput("Enter your choice: ");

            if (choice == FILTER_BY_PASSENGER_TYPE) {
                filterByPassengerType();
            } else if (choice == FILTER_BY_TIME_BAND) {
                filterByTimeBand();
            } else if (choice == FILTER_BY_ZONE) {
                filterByZone();
            } else if (choice == FILTER_BY_DATE) {
                filterByDate();
            } else if (choice == FILTER_BACK_TO_MAIN) {
                filterMenuActive = false;
            } else {
                System.out.println("Invalid choice! Please enter 1-5.");
            }
        }
    }

    private void displayFilterMenu() {
        System.out.println("\n========== FILTER MENU ==========");
        System.out.println("1. Filter by Passenger Type");
        System.out.println("2. Filter by Time Band");
        System.out.println("3. Filter by Zone (from or to)");
        System.out.println("4. Filter by Date");
        System.out.println("5. Back to Main Menu");
        System.out.println("=================================");
    }

    private void filterByPassengerType() {
        System.out.println("\n--- Filter by Passenger Type ---");
        System.out.println("Available types: ADULT, STUDENT, CHILD, SENIOR_CITIZEN");

        CityRideDataset.PassengerType type = getPassengerTypeInput();
        List<Journey> filtered = journeyManager.getJourneysByPassengerType(type);

        displayFilterResults(filtered, "Passenger Type: " + type);
    }

    private void filterByTimeBand() {
        System.out.println("\n--- Filter by Time Band ---");

        CityRideDataset.TimeBand band = getTimeBandInput();
        List<Journey> filtered = journeyManager.getJourneysByTimeBand(band);

        displayFilterResults(filtered, "Time Band: " + band);
    }

    private void filterByZone() {
        System.out.println("\n--- Filter by Zone ---");
        int zone = getZoneInput("Enter zone number (1-5): ");

        System.out.println("Filter by:");
        System.out.println("1. Starting zone (from)");
        System.out.println("2. Destination zone (to)");
        System.out.println("3. Either zone");

        int choice = getIntInput("Enter choice: ");

        List<Journey> filtered = null;

        if (choice == 1) {
            filtered = journeyManager.getJourneysByFromZone(zone);
        } else if (choice == 2) {
            filtered = journeyManager.getJourneysByToZone(zone);
        } else if (choice == 3) {
            filtered = journeyManager.getJourneysByZone(zone);
        } else {
            System.out.println("Invalid choice!");
            return;
        }

        displayFilterResults(filtered, "Zone: " + zone);
    }

    private void filterByDate() {
        System.out.println("\n--- Filter by Date ---");
        LocalDate date = getDateInput("Enter date (DD/MM/YYYY): ");
        List<Journey> filtered = journeyManager.getJourneysByDate(date);

        displayFilterResults(filtered, "Date: " + date.toString());
    }

    private void displayFilterResults(List<Journey> journeys, String filterCriteria) {
        System.out.println("\n--- Filter Results (" + filterCriteria + ") ---");

        if (journeys == null || journeys.isEmpty()) {
            System.out.println("No journeys match this filter.");
        } else {
            System.out.println("Found " + journeys.size() + " journey(s):");
            System.out.println("-".repeat(90));

            for (Journey journey : journeys) {
                System.out.println(journey);
            }

            // Calculate total for filtered journeys
            BigDecimal total = BigDecimal.ZERO;
            for (Journey journey : journeys) {
                total = total.add(journey.getChargedFare());
            }
            System.out.println("-".repeat(90));
            System.out.printf("Total cost for filtered journeys: £%.2f%n", total);
        }
    }

    private void displayDailySummary() {
        List<Journey> journeys = journeyManager.getAllJourneys();
        int totalJourneys = journeys.size();

        System.out.println("\n========== DAILY SUMMARY ==========");

        if (totalJourneys == 0) {
            System.out.println("No journeys recorded today.");
        } else {
            BigDecimal totalCost = journeyManager.getTotalCost();
            BigDecimal averageCost = journeyManager.getAverageCostPerJourney();
            Journey mostExpensive = journeyManager.getMostExpensiveJourney();

            System.out.println("Total number of journeys: " + totalJourneys);
            System.out.printf("Total cost charged: £%.2f%n", totalCost);
            System.out.printf("Average cost per journey: £%.2f%n", averageCost);

            if (mostExpensive != null) {
                System.out.printf("Most expensive journey: Journey #%d (£%.2f)%n",
                        mostExpensive.getJourneyId(), mostExpensive.getChargedFare());
            }

            // Category counts
            displayCategoryCounts();
        }

    }

    private void displayCategoryCounts() {
        Map<String, Integer> timeBandCounts = journeyManager.getTimeBandCounts();
        Map<String, Integer> zoneUsage = journeyManager.getZoneUsage();

        System.out.println("\n--- Category Counts ---");
        System.out.println("Peak journeys: " + timeBandCounts.get("PEAK"));
        System.out.println("Off-peak journeys: " + timeBandCounts.get("OFF_PEAK"));

        System.out.println("\nZone usage:");
        if (zoneUsage.isEmpty()) {
            System.out.println("  No zone usage data available.");
        } else {
            for (Map.Entry<String, Integer> entry : zoneUsage.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " time(s)");
            }
        }
    }

    private void displayTotalsByType() {
        System.out.println("\n========== TOTALS BY PASSENGER TYPE ==========");

        // Header
        System.out.printf("%-15s %10s %10s %10s %10s %12s%n",
                "Type", "Journeys", "Pre-Discount", "Discounted", "Charged", "Cap Reached");
        System.out.println("-".repeat(75));

        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            int journeyCount = journeyManager.getJourneyCountByType(type);
            BigDecimal preDiscountTotal = journeyManager.getPreDiscountTotalByType(type);
            BigDecimal discountedTotal = journeyManager.getDiscountedTotalByType(type);
            BigDecimal chargedTotal = journeyManager.getPassengerTotal(type);
            boolean capReached = journeyManager.isCapReached(type);

            System.out.printf("%-15s %10d £%8.2f £%8.2f £%8.2f %12s%n",
                    type, journeyCount, preDiscountTotal, discountedTotal, chargedTotal,
                    (capReached ? "YES" : "NO"));
        }
        System.out.println("=".repeat(75));
    }

    private void removeJourney() {
        System.out.println("\n--- Remove Journey ---");

        if (journeyManager.getJourneyCount() == 0) {
            System.out.println("No journeys to remove.");
            return;
        }

        listAllJourneys();
        int id = getIntInput("Enter journey ID to remove: ");

        Journey journeyToRemove = journeyManager.getJourneyById(id);

        if (journeyToRemove == null) {
            System.out.println("Error: Journey with ID " + id + " not found!");
        } else {
            System.out.print("Are you sure you want to remove journey " + id + "? (Y/N): ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            if (confirm.equals("Y")) {
                boolean removed = journeyManager.removeJourneyById(id);
                if (removed) {
                    System.out.println("Journey " + id + " removed successfully!");
                    System.out.println("All totals have been recalculated.");
                } else {
                    System.out.println("Error: Could not remove journey.");
                }
            } else {
                System.out.println("Removal cancelled.");
            }
        }
    }

    private void resetDay() {
        System.out.println("\n--- Reset Day ---");
        System.out.print("WARNING: This will delete ALL journeys. Are you sure? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            journeyManager.resetDay();
            System.out.println("✓ Day reset successfully! All journeys cleared.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }



    private LocalDate getDateInput(String prompt) {
        boolean valid = false;
        String input = "";

        while (valid == false) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return LocalDate.now();
            }

            if (InputValidator.isValidDate(input)) {
                valid = true;
            } else {
                System.out.println("Error: Invalid date format! Use DD/MM/YYYY");
            }
        }

        return InputValidator.parseDate(input);
    }

    private CityRideDataset.TimeBand getTimeBandInput() {
        boolean valid = false;
        CityRideDataset.TimeBand band = null;

        while (valid == false) {
            System.out.print("Enter time band (PEAK / OFF_PEAK): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (InputValidator.isValidTimeBand(input)) {
                band = InputValidator.parseTimeBand(input);
                valid = true;
            } else {
                System.out.println("Error: Invalid time band! Enter PEAK or OFF_PEAK");
            }
        }
        return band;
    }


    private int getZoneInput(String prompt) {
        boolean valid = false;
        int zone = -1;

        while (valid == false) {
            zone = getIntInput(prompt);

            if (InputValidator.isValidZone(zone)) {
                valid = true;
            } else {
                System.out.println("Error: Zone must be between 1 and 5");
            }
        }

        return zone;
    }

    private CityRideDataset.PassengerType getPassengerTypeInput() {
        boolean valid = false;
        CityRideDataset.PassengerType type = null;

        while (valid == false) {
            System.out.print("Enter passenger type (ADULT/STUDENT/CHILD/SENIOR_CITIZEN): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (InputValidator.isValidPassengerType(input)) {
                type = InputValidator.parsePassengerType(input);
                valid = true;
            } else {
                System.out.println("Error: Invalid passenger type!");
            }
        }

        return type;
    }


    private int getIntInput(String prompt) {
        boolean valid = false;
        int value = 0;

        while (valid == false) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Error: Input cannot be blank!");
            } else if (InputValidator.isValidInteger(input)) {
                value = Integer.parseInt(input);
                valid = true;
            } else {
                System.out.println("Error: Please enter a valid number!");
            }
        }

        return value;
    }
}